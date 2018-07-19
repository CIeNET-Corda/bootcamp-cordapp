package java_bootcamp;

import com.google.common.collect.ImmutableList;
import net.corda.core.contracts.ContractState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.TransactionState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.TransactionBuilder;

import java.security.PublicKey;
import java.util.List;

public class ScratchPad {
    public static void main(String[] args) {
        StateAndRef<ContractState> input = null;
        HouseState outputState = new HouseState("dummy street", null);
        PublicKey reqiredSigner = outputState.getOwner().getOwningKey();
        List<PublicKey> reqiredSigners = ImmutableList.of(reqiredSigner);

        Party notary = null;

        TransactionBuilder builder = new TransactionBuilder();
        builder.setNotary(notary);

        builder
                .addInputState(input)
                .addOutputState(outputState, "java_bootcamp.HourseContract")
                .addCommand(new HouseContract.Register(), reqiredSigners);

        //builder.verify();

    }
}
