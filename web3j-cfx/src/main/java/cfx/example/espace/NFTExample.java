package cfx.example.espace;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

public class NFTExample {

    static final String rpcUrl = "https://evm.confluxrpc.com";

    static Web3j web3;

    static final String ContractAddress = "0x2fc71B0e9a7362390EF7f3E9684d7Eb1c230863D";
    static final String MyAddress = "0x861caF1c18feE1F3e1De005C40f322e9c3eA774E";
    static Credentials credentials;

    public static void main(String[] args){
        web3 = Web3j.build(new HttpService(rpcUrl));
//        WalletUtils.

        credentials = Credentials.create("a26cda9060054ed2f95da339bc810b9fea25ada46846c48a8a166f523821be6e");
        System.out.println("钱包地址：" + credentials.getAddress());
        if (!MyAddress.equalsIgnoreCase(credentials.getAddress())){
            System.out.println("钱包地址异常");
            return;
        }

        try {
            // 查询余额
            long bal = balanceOf(ContractAddress,MyAddress);
            System.out.println("余额：" + bal);

            // 查询tokenid
            for (int i=0;i<bal;i++){
                long tid = tokenOfOwnerByIndex(ContractAddress,MyAddress,i);
                System.out.println("查询到tokenid="+ tid);
            }

            // 读取 tokenURI
            String tokenURI = tokenURI(ContractAddress,0);
            System.out.println("读取tokenuri="+ tokenURI);

            HashMap<Long,String> nfts = queryAllNFT(ContractAddress, MyAddress);
            for(Long key : nfts.keySet()){
                String value = nfts.get(key);
                System.out.println(key +"===" + value);
            }

            // 铸造
//            String jsonUri = "https://finelater.oss-cn-shenzhen.aliyuncs.com/2022-06-28/others/5baf40a9-73b3-448d-a22d-9d204484a6ec.json";
//            String hash = safeMint(ContractAddress,MyAddress,jsonUri);
//            System.out.println("调用铸造返回 hash=" + hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 余额查询
     * @param contractAddress
     * @param address
     * @return
     * @throws Exception
     */
    public static Long balanceOf(String contractAddress, String address) throws Exception {
        List input = Arrays.asList(new Address(address));
        List output = Arrays.asList(new TypeReference<Uint256>() {});

        Function function = new Function("balanceOf", input, output);
        String data = FunctionEncoder.encode(function);
        EthCall response = web3.ethCall(
                Transaction.createEthCallTransaction(MyAddress, contractAddress, data),
                DefaultBlockParameterName.LATEST)
                .send();

        List<Type> result = FunctionReturnDecoder.decode(
                response.getValue(), function.getOutputParameters());
        Uint256 balance = (Uint256) result.get(0);
        return balance.getValue().longValue();
    }

    /**
     * 读取指定账户对应下标的 tokenid
     * @param contractAddress
     * @param address
     * @param index
     * @return
     * @throws IOException
     */
    public static Long tokenOfOwnerByIndex(String contractAddress, String address,long index) throws IOException {
        List input = Arrays.asList(new Address(address),new Uint256(BigInteger.valueOf(index)));
        List output = Arrays.asList(new TypeReference<Uint256>() {});
        Function function = new Function("tokenOfOwnerByIndex", input, output);
        String data = FunctionEncoder.encode(function);
        EthCall response = web3.ethCall(Transaction.createEthCallTransaction(MyAddress, contractAddress, data), DefaultBlockParameterName.LATEST).send();
        List<Type> result = FunctionReturnDecoder.decode(
                response.getValue(), function.getOutputParameters());
        if (0 == result.size()){
            return null;
        }
        Uint256 tokenid = (Uint256) result.get(0);
        return tokenid.getValue().longValue();
    }

    /**
     * 读取tokenid的uri
     * @param contractAddress
     * @param tokenId
     * @return
     * @throws IOException
     */
    public static String tokenURI(String contractAddress,long tokenId) throws IOException {
        List input = Arrays.asList(new Uint256(BigInteger.valueOf(tokenId)));
        List output = Arrays.asList(new TypeReference<Utf8String>() {});
        Function function = new Function("tokenURI", input, output);
        String data = FunctionEncoder.encode(function);
        EthCall response = web3.ethCall(Transaction.createEthCallTransaction(MyAddress, contractAddress, data), DefaultBlockParameterName.LATEST).send();
        List<Type> result = FunctionReturnDecoder.decode(
                response.getValue(), function.getOutputParameters());
        if (0 == result.size()){
            return null;
        }
        Utf8String tokenuri = (Utf8String) result.get(0);
        return tokenuri.getValue();
    }

    /**
     * 读取账户所有 NFT
     * @param contractAddress
     * @param address
     * @return
     * @throws Exception
     */
    public static HashMap<Long,String> queryAllNFT(String contractAddress,String address) throws Exception {

        HashMap<Long,String> result = new HashMap();

        long bal = balanceOf(ContractAddress,MyAddress);
        for (int i=0;i<bal;i++){
            long tid = tokenOfOwnerByIndex(ContractAddress,MyAddress,i);
            String tokenURI = tokenURI(ContractAddress,tid);
            result.put(tid,tokenURI);
        }
        return result;
    }


    /**
     * 铸造1个
     * @param contractAddress
     * @param toAddress
     * @param uri
     * @return
     * @throws Exception
     */
    public static String safeMint(String contractAddress,String toAddress,String uri) throws Exception {
        List input = Arrays.asList(new Address(toAddress),new Utf8String(uri));
        List output = Arrays.asList(new TypeReference<Utf8String>() {});

        Function function = new Function(
                "safeMint",input,output);
        String data = FunctionEncoder.encode(function);

        BigInteger nonce = getNonce(credentials.getAddress());
        System.out.println("nonce：" + nonce);

        RawTransaction transaction = RawTransaction.createTransaction(nonce, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT, contractAddress, data);
        byte[] signedMessage = TransactionEncoder.signMessage(transaction,credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
        return ethSendTransaction.getTransactionHash();
    }

    private static BigInteger getNonce(String address) throws Exception {
        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST) .sendAsync() .get();
        return ethGetTransactionCount.getTransactionCount();
    }

}
