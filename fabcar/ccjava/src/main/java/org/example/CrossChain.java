package org.example;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.6.1.
 */
@SuppressWarnings("rawtypes")
public class CrossChain extends Contract {
    public static final String BINARY = "60806040526006805463ffffffff1916905534801561001d57600080fd5b50610b158061002d6000396000f3fe608060405234801561001057600080fd5b50600436106100935760003560e01c80638991deb5116100665780638991deb5146103e657806398aca922146103ee5780639f05a36d146103f6578063ab9dbd0714610412578063fe4fab401461042c57610093565b80633bc5de30146100985780635596b972146101155780635e01eb5a146103d65780635ec94884146103de575b600080fd5b6100a0610449565b6040805160208082528351818301528351919283929083019185019080838360005b838110156100da5781810151838201526020016100c2565b50505050905090810190601f1680156101075780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6103d4600480360360c081101561012b57600080fd5b81359190810190604081016020820135600160201b81111561014c57600080fd5b82018360208201111561015e57600080fd5b803590602001918460018302840111600160201b8311171561017f57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b8111156101d157600080fd5b8201836020820111156101e357600080fd5b803590602001918460018302840111600160201b8311171561020457600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561025657600080fd5b82018360208201111561026857600080fd5b803590602001918460018302840111600160201b8311171561028957600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b8111156102db57600080fd5b8201836020820111156102ed57600080fd5b803590602001918460018302840111600160201b8311171561030e57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561036057600080fd5b82018360208201111561037257600080fd5b803590602001918460018302840111600160201b8311171561039357600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295506104e0945050505050565b005b6100a0610805565b6100a0610865565b6100a06108c6565b6100a0610927565b6103fe610985565b604080519115158252519081900360200190f35b61041a61098e565b60408051918252519081900360200190f35b6103d46004803603602081101561044257600080fd5b5035610994565b60038054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156104d55780601f106104aa576101008083540402835291602001916104d5565b820191906000526020600020905b8154815290600101906020018083116104b857829003601f168201915b505050505090505b90565b600086905584516104f8906001906020880190610a47565b50835161050c906002906020870190610a47565b508251610520906003906020860190610a47565b508151610534906004906020850190610a47565b508051610548906005906020840190610a47565b507fb8245865dd25c28347d41e8d30218ef1b951aaf5336a6e4673495458a218b8786000546001600260036004600560405180878152602001806020018060200180602001806020018060200186810386528b8181546001816001161561010002031660029004815260200191508054600181600116156101000203166002900480156106165780601f106105eb57610100808354040283529160200191610616565b820191906000526020600020905b8154815290600101906020018083116105f957829003601f168201915b505086810385528a54600260001961010060018416150201909116048082526020909101908b90801561068a5780601f1061065f5761010080835404028352916020019161068a565b820191906000526020600020905b81548152906001019060200180831161066d57829003601f168201915b505086810384528954600260001961010060018416150201909116048082526020909101908a9080156106fe5780601f106106d3576101008083540402835291602001916106fe565b820191906000526020600020905b8154815290600101906020018083116106e157829003601f168201915b50508681038352885460026000196101006001841615020190911604808252602090910190899080156107725780601f1061074757610100808354040283529160200191610772565b820191906000526020600020905b81548152906001019060200180831161075557829003601f168201915b50508681038252875460026000196101006001841615020190911604808252602090910190889080156107e65780601f106107bb576101008083540402835291602001916107e6565b820191906000526020600020905b8154815290600101906020018083116107c957829003601f168201915b50509b50505050505050505050505060405180910390a1505050505050565b60018054604080516020601f600260001961010087891615020190951694909404938401819004810282018101909252828152606093909290918301828280156104d55780601f106104aa576101008083540402835291602001916104d5565b60048054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156104d55780601f106104aa576101008083540402835291602001916104d5565b60058054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156104d55780601f106104aa576101008083540402835291602001916104d5565b60028054604080516020601f60001961010060018716150201909416859004938401819004810282018101909252828152606093909290918301828280156104d55780601f106104aa576101008083540402835291602001916104d5565b60065460ff1690565b60005490565b80600214156109b1576006805461ff0019166101001790556109ed565b80600314156109d0576006805462ff00001916620100001790556109ed565b80600414156109ed576006805463ff000000191663010000001790555b60065460ff6101009091041615156001148015610a17575060065462010000900460ff1615156001145b8015610a3157506006546301000000900460ff1615156001145b15610a44576006805460ff191660011790555b50565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f10610a8857805160ff1916838001178555610ab5565b82800160010185558215610ab5579182015b82811115610ab5578251825591602001919060010190610a9a565b50610ac1929150610ac5565b5090565b6104dd91905b80821115610ac15760008155600101610acb56fea2646970667358221220c35bdc9c6edd4457c912422838c55ddf6a842c9337991e2c6dfbd4645de3cc1664736f6c634300060b0033";

    public static final String FUNC_CLOSECHAIN = "CloseChain";

    public static final String FUNC_GETDATA = "getData";

    public static final String FUNC_GETEND = "getEnd";

    public static final String FUNC_GETID = "getID";

    public static final String FUNC_GETRECEIVER = "getReceiver";

    public static final String FUNC_GETRECEIVERCHAIN = "getReceiverChain";

    public static final String FUNC_GETSENDER = "getSender";

    public static final String FUNC_GETSENDERCHAIN = "getSenderChain";

    public static final String FUNC_SENDCROSSCHAIN = "sendCrossChain";

    public static final Event END_EVENT = new Event("End", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
    ;

    public static final Event TRANSFER_EVENT = new Event("Transfer", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}));
    ;

    @Deprecated
    protected CrossChain(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CrossChain(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected CrossChain(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected CrossChain(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<EndEventResponse> getEndEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(END_EVENT, transactionReceipt);
        ArrayList<EndEventResponse> responses = new ArrayList<EndEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            EndEventResponse typedResponse = new EndEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.isEnd = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<EndEventResponse> endEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, EndEventResponse>() {
            @Override
            public EndEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(END_EVENT, log);
                EndEventResponse typedResponse = new EndEventResponse();
                typedResponse.log = log;
                typedResponse.isEnd = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<EndEventResponse> endEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(END_EVENT));
        return endEventFlowable(filter);
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(TRANSFER_EVENT, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.chainid = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.sender = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.receiver = (String) eventValues.getNonIndexedValues().get(2).getValue();
            typedResponse.data = (String) eventValues.getNonIndexedValues().get(3).getValue();
            typedResponse.senderchain = (String) eventValues.getNonIndexedValues().get(4).getValue();
            typedResponse.receiverchain = (String) eventValues.getNonIndexedValues().get(5).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<TransferEventResponse> transferEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(TRANSFER_EVENT, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.log = log;
                typedResponse.chainid = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.sender = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.receiver = (String) eventValues.getNonIndexedValues().get(2).getValue();
                typedResponse.data = (String) eventValues.getNonIndexedValues().get(3).getValue();
                typedResponse.senderchain = (String) eventValues.getNonIndexedValues().get(4).getValue();
                typedResponse.receiverchain = (String) eventValues.getNonIndexedValues().get(5).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<TransferEventResponse> transferEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(TRANSFER_EVENT));
        return transferEventFlowable(filter);
    }

    public RemoteFunctionCall<TransactionReceipt> CloseChain(BigInteger i) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_CLOSECHAIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(i)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> getData() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETDATA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> getEnd() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETEND, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> getID() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> getReceiver() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETRECEIVER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getReceiverChain() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETRECEIVERCHAIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getSender() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSENDER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getSenderChain() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETSENDERCHAIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> sendCrossChain(BigInteger _chainid, String _sender, String _receiver, String _data, String _senderchain, String _receiverchain) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_SENDCROSSCHAIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Int256(_chainid), 
                new org.web3j.abi.datatypes.Utf8String(_sender), 
                new org.web3j.abi.datatypes.Utf8String(_receiver), 
                new org.web3j.abi.datatypes.Utf8String(_data), 
                new org.web3j.abi.datatypes.Utf8String(_senderchain), 
                new org.web3j.abi.datatypes.Utf8String(_receiverchain)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static CrossChain load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CrossChain(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static CrossChain load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CrossChain(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static CrossChain load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new CrossChain(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static CrossChain load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new CrossChain(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<CrossChain> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CrossChain.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CrossChain> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CrossChain.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<CrossChain> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(CrossChain.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<CrossChain> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CrossChain.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static class EndEventResponse extends BaseEventResponse {
        public Boolean isEnd;
    }

    public static class TransferEventResponse extends BaseEventResponse {
        public BigInteger chainid;

        public String sender;

        public String receiver;

        public String data;

        public String senderchain;

        public String receiverchain;
    }
}
