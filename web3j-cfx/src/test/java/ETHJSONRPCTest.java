import org.junit.Before;
import org.junit.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.http.HttpService;

import java.util.concurrent.ExecutionException;

public class ETHJSONRPCTest {

    public Web3j web3;
    public final static  String Account1 = "0xec4a373b885CB6AcE3340A0ae83F249Dc3890F77";

    @Before
    public void before(){
        web3 = Web3j.build(new HttpService("http://127.0.0.1:7545"));
    }

    @Test
    public void getBlockNumber() throws ExecutionException, InterruptedException {
        EthBlockNumber result = web3.ethBlockNumber().sendAsync().get();
        println("获得区块编号");
        println(result.getBlockNumber());
    }

    @Test
    public void getEthAccounts() throws ExecutionException, InterruptedException {
        EthAccounts result = web3.ethAccounts().sendAsync().get();
        println("获得钱包账户");
        println(result.getAccounts());
    }

    @Test
    public void getTransactionCount() throws ExecutionException, InterruptedException {
        EthGetTransactionCount result = web3.ethGetTransactionCount(Account1,
                DefaultBlockParameter.valueOf("latest")).sendAsync().get();
        println("账户交易次数");
        println(result.getTransactionCount());
    }

    @Test
    public void getEthBalance() throws ExecutionException, InterruptedException {
        EthGetBalance result = web3.ethGetBalance(Account1,
                DefaultBlockParameter.valueOf("latest")).sendAsync().get();
        println("账户余额");
        println(result.getBalance());
    }

    public static void println(Object obj){
        System.out.println(obj);
    }
}