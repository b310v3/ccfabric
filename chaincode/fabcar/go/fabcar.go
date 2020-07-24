/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package main

import (
	"encoding/json"
	"fmt"
	"log"

	//"strconv"

	"github.com/hyperledger/fabric-contract-api-go/contractapi"
)

// SmartContract provides functions for managing a car
type SmartContract struct {
	contractapi.Contract
}

// Car describes basic details of what makes up a car
type Car struct {
	Make   string `json:"make"`
	Model  string `json:"model"`
	Colour string `json:"colour"`
	Owner  string `json:"owner"`
}

// QueryResult structure used for handling result of query
type QueryResult struct {
	Key    string `json:"Key"`
	Record *Car
}

type Ccpeer struct {
	Peer []string `json:"peer"`
}

type CrossChainInfo struct {
	Ownerpeer   string `json:"ownerpeer"`
	Ownerchain  string `json:"ownerchain"`
	CCpeer      string `json:"ccpeer"`
	Targetchian string `json:"targetchain"`
	Info        string `json:"info"`
}

// InitLedger adds a base set of cars to the ledger
func (s *SmartContract) InitLedger(ctx contractapi.TransactionContextInterface) error {
	peerlist := []string{"peer0.org2.example.com"}
	ccpeer := Ccpeer{Peer: peerlist}

	ccpeerAsBytes, _ := json.Marshal(ccpeer)
	err := ctx.GetStub().PutState("ccpeer", ccpeerAsBytes)

	if err != nil {
		return fmt.Errorf("Failed to put to world state. %s", err.Error())
	}

	return nil
}

// Query the ccpeer from state (maybe want to return alive peer)
func (s *SmartContract) Queryccpeer(ctx contractapi.TransactionContextInterface) (*Ccpeer, error) {
	ccpeerAsBytes, err := ctx.GetStub().GetState("ccpeer")

	if err != nil {
		return nil, fmt.Errorf("Failed to read from world state. %s", err.Error())
	}

	if ccpeerAsBytes == nil {
		return nil, fmt.Errorf("%s does not exist", "ccpeer")
	}

	ccpeer := new(Ccpeer)
	_ = json.Unmarshal(ccpeerAsBytes, ccpeer)

	return ccpeer, nil
}

// Insert crosschain peer into the world state
func (s *SmartContract) Insertccpeer(ctx contractapi.TransactionContextInterface, peer string) error {
	ccpeer, err := s.Queryccpeer(ctx)

	if err != nil {
		return err
	}

	ccpeer.Peer = append(ccpeer.Peer, peer)

	ccpeerAsBytes, _ := json.Marshal(ccpeer)

	return ctx.GetStub().PutState("ccpeer", ccpeerAsBytes)
}

// insert a crosschian transaction to the ledger
func (s *SmartContract) Ccaction(ctx contractapi.TransactionContextInterface, ownerpeer string, ownerchain string, ccpeer string, targetchain string, info string) error {
	ccinfo := CrossChainInfo{
		Ownerpeer:   ownerpeer,
		Ownerchain:  ownerchain,
		CCpeer:      ccpeer,
		Targetchian: targetchain,
		Info:        info,
	}

	ccinfoAsBytes, _ := json.Marshal(ccinfo)

	err := ctx.GetStub().SetEvent("crosschain", ccinfoAsBytes)
	if err != nil {
		log.Fatal(err)
	}

	return ctx.GetStub().PutState("crosschain", ccinfoAsBytes)
}

func main() {

	chaincode, err := contractapi.NewChaincode(new(SmartContract))

	if err != nil {
		fmt.Printf("Error create crosschain chaincode: %s", err.Error())
		return
	}

	if err := chaincode.Start(); err != nil {
		fmt.Printf("Error starting crosschian chaincode: %s", err.Error())
	}
}
