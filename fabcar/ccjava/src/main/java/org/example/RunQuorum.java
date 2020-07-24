package org.example;

import java.io.*;
import java.lang.Exception;
import java.lang.InterruptedException;
import java.math.BigInteger;
import java.util.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import okhttp3.OkHttpClient;

import org.apache.commons.io.FileUtils;
import org.graalvm.compiler.nodes.NodeView.Default;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.tx.ClientTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.http.HttpService;
import org.web3j.quorum.Quorum;
import org.web3j.quorum.enclave.Enclave;
import org.web3j.quorum.enclave.Tessera;
import org.web3j.quorum.enclave.protocol.EnclaveService;
import org.web3j.quorum.tx.QuorumTransactionManager;

public class RunQuorum {
    private final static String PASSWORD = "6191";
    private static CrossChain contract;

    public void startQuorum() throws Exception {
        closeChain();
        String s;
        Process p;
        BufferedReader br;

        try {
            p = Runtime.getRuntime().exec("/home/belove/quorum/build/bin/geth --datadir /home/belove/quorum/fromscratch/new-node-1 init /home/belove/quorum/fromscratch/genesis.json");
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line : " + s);
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        
            p = Runtime.getRuntime().exec("sh /home/belove/quorum/fromscratch/startnode1.sh");
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)
                System.out.println("line : " + s);
            p.waitFor();
            System.out.println("exit: " + p.exitValue());
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.out.println("Quorum is up and run!");;
    }

    public String Deploy() throws Exception{
        String contractAddress = "";
        
        try {
            Quorum web3 = Quorum.build(new HttpService("http://localhost:22000")); // connect to the quorum node
            Credentials credentials = WalletUtils.loadCredentials(PASSWORD, "/home/belove/quorum/fromscratch/new-node-1/keystore/UTC--2020-07-23T14-59-43.886422698Z--7e2e058ea0717d63da6c7791acee54fd8eb0e0c2");
            
            contract = CrossChain.deploy(web3, credentials, BigInteger.valueOf(0x0), DefaultGasProvider.GAS_LIMIT).send();
            
            contractAddress = contract.getContractAddress();
            System.out.println(contractAddress);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return contractAddress;

    }

    public String[] Deploy(String contractAddress) throws Exception{
        //contractAddress = "0x" + contractAddress;
        String[] ccdata = new String[5];
        
        try {
            Web3j web3 = Web3j.build(new HttpService("http://localhost:22000")); // connect to the quorum node
            Credentials credentials = WalletUtils.loadCredentials(PASSWORD, "/home/belove/quorum/fromscratch/new-node-1/keystore/UTC--2020-07-23T14-59-43.886422698Z--7e2e058ea0717d63da6c7791acee54fd8eb0e0c2");

            contract = CrossChain.load(contractAddress, web3, credentials, BigInteger.valueOf(0x0), DefaultGasProvider.GAS_LIMIT);
            System.out.println("Get the Contract!");
            ccdata[0] = contract.getSender().send();
            ccdata[1] = contract.getReceiver().send();
            ccdata[2] = contract.getData().send();
            ccdata[3] = contract.getSenderChain().send();
            ccdata[4] = contract.getReceiverChain().send();

            // sending close chain info
            contract.CloseChain().send();
            closeChain();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return ccdata;
    }

    public void PushCrossChain(String sender, String receiver, String data, String senderchain, String receiverchain) throws Exception {
        Random rand = new Random();
        contract.sendCrossChain(BigInteger.valueOf(rand.nextInt(99999)), sender, receiver, data, senderchain, receiverchain).send();
        System.out.println("Success pass the data to the crosschain");
    }

    public boolean CheckClose() throws Exception{
        boolean check = contract.getReceived().send();
        if (check == true){
            closeChain();
            return true;
        }
        return false;
    }

    private void closeChain() throws IOException{
        Runtime.getRuntime().exec("killall -INT geth && killall constellation-node");
        File f;

        f = new File("/home/belove/quorum/fromscratch/new-node-1/geth");
        if (f.exists()){
            FileUtils.forceDelete(f);
        }
        f = new File("/home/belove/quorum/fromscratch/new-node-1/quorum-raft-state");
        if (f.exists()){
            FileUtils.forceDelete(f);
        }
        f = new File("/home/belove/quorum/fromscratch/new-node-1/raft-snap");
        if (f.exists()){
            FileUtils.forceDelete(f);
        }
        f = new File("/home/belove/quorum/fromscratch/new-node-1/raft-wal");
        if (f.exists()){
            FileUtils.forceDelete(f);
        }
    }
}