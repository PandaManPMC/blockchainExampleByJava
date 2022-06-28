package cfx.example.core;

import conflux.web3j.*;
import conflux.web3j.types.Address;
import org.web3j.abi.datatypes.Utf8String;

public class CallMint {

    public static final String privateKey = "73ae013380277a27e66842fbeee965b4b86fc66f2c555c7ecd350611a9619bb4";
    public static final String password = "123456";
    public static final String myWallet = "cfxtest:aakn1vwze9142dfh3hsvt4xz350xw6gfkyup2w603k";

    // test
    public static final String confluxrpcUrl = "https://portal-test.confluxrpc.com";
    public static final String contractAddress = "cfxtest:achnssh9rwhx5ycccbu3vrsnrehw3rrr1ar763513f";
    public static final String chainId = "0x1";
    public static final Integer mynetworkId = 1;

    public static void main(String[] args) throws Exception {
        Cfx cfx = Cfx.create(confluxrpcUrl, 3, 1000);
        Address cAddress = new Address(contractAddress);

        Account account = Account.create(cfx, privateKey);
        Account.Option option = new Account.Option();
        option.withChainId(mynetworkId);
//             调用合约 safeMint 支付 0.1 cfx 铸造 NFT
//            function safeMint(address to, string memory uri) external payable {
//                require(to != address(0), "to address error");
//                require(msg.value == 0.1 ether);
//                uint256 tokenId = totalSupply();
//                _safeMint(to, tokenId);
//                _setTokenURI(tokenId, uri);
//            }
        String metaDataUri = "ipfs://Qmf7jXyZkLjk8m7VTrkTG6Ww8rPHW3xqb51VfcgiCcEP81";
        option.withValue(CfxUnit.cfx2Drip(0.1));
        String txHash = account.call(option, cAddress, "safeMint",new Address(myWallet).getABIAddress(),new Utf8String(metaDataUri));
        System.out.println("tx hash: " + txHash);



        // 转账NFT
//        Address to = new Address("cfxtest:aathg321h7sys2gw30v238rcsczzbb575e036wkcdg");
//        txHash = account.call(option, cAddress, "safeTransferFrom",new Address(myWallet).getABIAddress(),to.getABIAddress(),new Uint256(11));
    }

}
