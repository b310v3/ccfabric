package org.example;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Int256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
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
    public static final String BINARY = "6080604052600060065534801561001557600080fd5b506107b1806100256000396000f3fe608060405234801561001057600080fd5b50600436106100935760003560e01c80635ec94884116100665780635ec94884146103fa5780637765968b146104025780638991deb51461040a57806398aca92214610412578063ab9dbd071461041a57610093565b80633bc5de30146100985780635596b9721461011557806355d0ad18146103d65780635e01eb5a146103f2575b600080fd5b6100a0610434565b6040805160208082528351818301528351919283929083019185019080838360005b838110156100da5781810151838201526020016100c2565b50505050905090810190601f1680156101075780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6103d4600480360360c081101561012b57600080fd5b81359190810190604081016020820135600160201b81111561014c57600080fd5b82018360208201111561015e57600080fd5b803590602001918460018302840111600160201b8311171561017f57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b8111156101d157600080fd5b8201836020820111156101e357600080fd5b803590602001918460018302840111600160201b8311171561020457600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561025657600080fd5b82018360208201111561026857600080fd5b803590602001918460018302840111600160201b8311171561028957600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b8111156102db57600080fd5b8201836020820111156102ed57600080fd5b803590602001918460018302840111600160201b8311171561030e57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561036057600080fd5b82018360208201111561037257600080fd5b803590602001918460018302840111600160201b8311171561039357600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295506104cb945050505050565b005b6103de61053c565b604080519115158252519081900360200190f35b6100a0610557565b6100a06105b7565b6103d4610618565b6100a0610623565b6100a0610684565b6104226106e2565b60408051918252519081900360200190f35b60038054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156104c05780601f10610495576101008083540402835291602001916104c0565b820191906000526020600020905b8154815290600101906020018083116104a357829003601f168201915b505050505090505b90565b600086905584516104e39060019060208801906106e8565b5083516104f79060029060208701906106e8565b50825161050b9060039060208601906106e8565b50815161051f9060049060208501906106e8565b5080516105339060059060208401906106e8565b50505050505050565b600060065460031415610551575060016104c8565b50600090565b60018054604080516020601f600260001961010087891615020190951694909404938401819004810282018101909252828152606093909290918301828280156104c05780601f10610495576101008083540402835291602001916104c0565b60048054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156104c05780601f10610495576101008083540402835291602001916104c0565b600680546001019055565b60058054604080516020601f60026000196101006001881615020190951694909404938401819004810282018101909252828152606093909290918301828280156104c05780601f10610495576101008083540402835291602001916104c0565b60028054604080516020601f60001961010060018716150201909416859004938401819004810282018101909252828152606093909290918301828280156104c05780601f10610495576101008083540402835291602001916104c0565b60005490565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061072957805160ff1916838001178555610756565b82800160010185558215610756579182015b8281111561075657825182559160200191906001019061073b565b50610762929150610766565b5090565b5b80821115610762576000815560010161076756fea264697066735822122080315467df02ab2894d96557ee5b7d501cdc671db1b30b83b2e33f9434241bab64736f6c634300060c0033";

    public static final String FUNC_CLOSECHAIN = "CloseChain";

    public static final String FUNC_GETDATA = "getData";

    public static final String FUNC_GETID = "getID";

    public static final String FUNC_GETRECEIVED = "getReceived";

    public static final String FUNC_GETRECEIVER = "getReceiver";

    public static final String FUNC_GETRECEIVERCHAIN = "getReceiverChain";

    public static final String FUNC_GETSENDER = "getSender";

    public static final String FUNC_GETSENDERCHAIN = "getSenderChain";

    public static final String FUNC_SENDCROSSCHAIN = "sendCrossChain";

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

    public RemoteFunctionCall<TransactionReceipt> CloseChain() {
        final Function function = new Function(
                FUNC_CLOSECHAIN, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> getData() {
        final Function function = new Function(FUNC_GETDATA, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getID() {
        final Function function = new Function(FUNC_GETID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Int256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> getReceived() {
        final Function function = new Function(FUNC_GETRECEIVED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<String> getReceiver() {
        final Function function = new Function(FUNC_GETRECEIVER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getReceiverChain() {
        final Function function = new Function(FUNC_GETRECEIVERCHAIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getSender() {
        final Function function = new Function(FUNC_GETSENDER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getSenderChain() {
        final Function function = new Function(FUNC_GETSENDERCHAIN, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> sendCrossChain(BigInteger _chainid, String _sender, String _receiver, String _data, String _senderchain, String _receiverchain) {
        final Function function = new Function(
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
}
