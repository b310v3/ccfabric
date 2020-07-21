package main.java.org.example;

import org.web3j.core;

public class RunQuorum {
    public void CrossChain() {
        Runtime.getRuntime().exec("cd /home/belove/quorum/fromscratch && geth --datadir new-node-1 init genesis.json");
        Runtime.getRuntime().exec("chmod +x /home/belove/quorum/fromscratchstartnode1.sh");
        Runtime.getRuntime().exec("/home/belove/quorum/fromscratch/startnode1.sh");
        
        
    }
}