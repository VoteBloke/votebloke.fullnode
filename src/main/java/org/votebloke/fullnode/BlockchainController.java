package org.votebloke.fullnode;

import blockchain.TransactionOutput;
import org.springframework.web.bind.annotation.*;

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
    public ArrayList<TransactionOutput> getAllTransactions() {
        return chain.getAllOutputTransactions();
    }

    @PostMapping("/elections")
    public String callElections(@RequestBody ElectionsPostBody body) {
        chain.callElections(body.elections, body.answers);
        return "Elections called";
    }
}
