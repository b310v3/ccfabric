package org.example;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class RunQuorum {
    private final static String PASSWORD = "oc886191";
    private static CrossChain contract;
    //private final BigInteger GAS = 0;

    public String Deploy() throws Exception{
        Runtime.getRuntime().exec("cd /home/belove/quorum && export PATH=$(pwd)/build/bin:$PATH");
        Runtime.getRuntime().exec("cd /home/belove/quorum/fromscratch && geth --datadir new-node-1 init genesis.json");
        Runtime.getRuntime().exec("chmod +x /home/belove/quorum/fromscratch/startnode1.sh");
        Runtime.getRuntime().exec("sh /home/belove/quorum/fromscratch/startnode1.sh");
        
        Web3j web3 = Web3j.build(new HttpService("http://localhost:22000")); // connect to the quorum node
        Credentials credentials = WalletUtils.loadCredentials(PASSWORD, "/home/belove/quorum/fromscratch/new-node-1/keystore/UTC--2020-07-15T07-10-18.466074174Z--01e4300aec7188d7108880de4fbf2f0691ec797c");

        contract = CrossChain.deploy(web3, credentials, BigInteger.ZERO, BigInteger.ZERO).send();
        String contractAddress = contract.getContractAddress();
        return contractAddress;
    }

    public String[] Deploy(String contractAddress, int i) throws Exception{
        //contractAddress = "0x" + contractAddress;
        Runtime.getRuntime().exec("cd /home/belove/quorum && export PATH=$(pwd)/build/bin:$PATH");
        Runtime.getRuntime().exec("cd /home/belove/quorum/fromscratch && geth --datadir new-node-1 init genesis.json");
        Runtime.getRuntime().exec("chmod +x /home/belove/quorum/fromscratch/startnode1.sh");
        Runtime.getRuntime().exec("sh /home/belove/quorum/fromscratch/startnode1.sh");

        Web3j web3 = Web3j.build(new HttpService("http://localhost:22000")); // connect to the quorum node
        Credentials credentials = WalletUtils.loadCredentials(PASSWORD, "/home/belove/quorum/fromscratch/new-node-1/keystore/UTC--2020-07-15T07-10-18.466074174Z--01e4300aec7188d7108880de4fbf2f0691ec797c");

        contract = CrossChain.load(contractAddress, web3, credentials, BigInteger.ZERO, BigInteger.ZERO);
        System.out.println("Get the Contract!");
        String sender = contract.getSender().send();
        String receiver = contract.getReceiver().send();
        String data = contract.getData().send();
        String senderchain = contract.getSenderChain().send();
        String receiverchain = contract.getReceiverChain().send();
        String[] ccdata = {sender, receiver, data, senderchain, receiverchain};

        // sending close chain info
        contract.CloseChain(BigInteger.valueOf(i)).send();
        closeChain();
        return ccdata;
    }

    public void PushCrossChain(String sender, String receiver, String data, String senderchain, String receiverchain) throws Exception {
        Random rand = new Random();
        contract.sendCrossChain(BigInteger.valueOf(rand.nextInt(99999)), sender, receiver, data, senderchain, receiverchain).send();
        System.out.println("Success pass the data to the crosschain");
    }

    public boolean CheckClose() throws Exception{
        boolean check = contract.getEnd().send();
        if (check == true){
            closeChain();
            return true;
        }
        return false;
    }

    private void closeChain() throws IOException{
        Runtime.getRuntime().exec("killall -INT geth && killall constellation-node");
    }
    
}