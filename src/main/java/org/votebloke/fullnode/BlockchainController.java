package org.votebloke.fullnode;

import blockchain.TransactionOutput;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/v1/blockchain")
public class BlockchainController {
    private final BlockchainModel chain = new BlockchainModel();

    @GetMapping("/accounts/create")
    public String createAccount() {
        chain.createAccount();
        return "Account created";
    }

    @GetMapping("/transactions/")
    @ResponseBody
    public ArrayList<TransactionGetBody> getAllTransactions() {
        return chain.getAllOutputTransactions();
    }

    @PostMapping("/elections")
    public String callElections(@RequestBody ElectionsPostBody body) {
        chain.callElections(body.elections, body.answers);
        return "Elections called";
    }
}
