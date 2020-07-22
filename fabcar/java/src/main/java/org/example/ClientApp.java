/*
SPDX-License-Identifier: Apache-2.0
*/

package org.example;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.function.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeUnit;

import javax.crypto.KeyAgreement;

import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractEvent;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class ClientApp {

	private static Consumer<ContractEvent> contractListener;
	private static final BlockingQueue<ContractEvent> contractEvents = new LinkedBlockingQueue<>();

	static {
		System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
	}

	public static void main(final String[] args) throws Exception {
		
		// Load a file system based wallet for managing identities.
		final Path walletPath = Paths.get("wallet");
		final Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		// load a CCP
		final Path networkConfigPath = Paths.get("..", "..", "test-network", "organizations", "peerOrganizations", "org1.example.com", "connection-org1.yaml");

		final Gateway.Builder builder = Gateway.createBuilder();
		builder.identity(wallet, "appUser").networkConfig(networkConfigPath).discovery(true);

		// create a gateway connection
		try (Gateway gateway = builder.connect()) {

			// get the network and contract
			final Network network = gateway.getNetwork("mychannel");
			final Contract contract = network.getContract("fabcar");

			//contract.submitTransaction("Insertccpeer", "peer01");
			//contract.submitTransaction("Insertccpeer", "peer02");



			byte[] result;

			result = contract.evaluateTransaction("Queryccpeer");
			System.out.println(new String(result));
			final JSONObject peers = new JSONObject(new String(result));

			String temp = peers.valueToString(peers.get("peer"));
			temp = temp.substring(1, temp.length() - 1);
			final String[] peerlist = temp.split(",");

			final Random rand = new Random();
			final String peer = peerlist[(rand.nextInt(peerlist.length))];
			System.out.println(peer + " is the winner!");

			contract.submitTransaction("Ccaction", "peer0.org1.example.com", "A-fabirc-chain", "peer0.org1.example.com", "B-fabric-chain", "Happy Crosschain!");//peer.replace("\"", "")
			/*
			contractListener = contract.addContractListener(contractEvents::add, "crosschain");
			ClientApp app = new ClientApp();
			ContractEvent event = app.getContractEvent("peer");
			System.out.println(event.getPayload().toString());
			*/
			// contract.submitTransaction("validate"/* work on progess for validation */);

			/*
			 * contract.submitTransaction("createCar", "CAR10", "VW", "Polo", "Grey",
			 * "Mary");
			 * 
			 * result = contract.evaluateTransaction("queryCar", "CAR10");
			 * System.out.println(new String(result));
			 * 
			 * contract.submitTransaction("changeCarOwner", "CAR10", "Archie");
			 * 
			 * result = contract.evaluateTransaction("queryCar", "CAR10");
			 * System.out.println(new String(result));
			 */
		}
	}

	private ContractEvent getContractEvent(String expectedPayload) throws InterruptedException {
		final List<String> payloads = new ArrayList<>();
		final ContractEvent matchingEvent = removeFirstMatch(contractEvents, event -> {
			String eventPayload = event.getPayload().toString();
			return expectedPayload.equals(eventPayload);
		});
		
		return matchingEvent;
	}

	/**
	 * Remove and return the first element matching the given predicate. All other
	 * elements remain on the queue.
	 * 
	 * @param queue A queue.
	 * @param match Filter used to match queue elements.
	 * @return The first matching element or null if no matches are found.
	 * @throws InterruptedException If waiting for queue elements is interrupted.
	 */
	private <T> T removeFirstMatch(final BlockingQueue<T> queue, final Predicate<? super T> match)
			throws InterruptedException {
		final List<T> unmatchedElements = new ArrayList<>();
        T element;

        while ((element = queue.poll()) != null) {
            if (match.test(element)) {
                break;
            }
            unmatchedElements.add(element);
        }

        queue.addAll(unmatchedElements); // Re-queue elements that didn't match
        return element;
    }
}