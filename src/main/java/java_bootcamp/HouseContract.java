package java_bootcamp;

import net.corda.core.contracts.Command;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.Contract;
import net.corda.core.contracts.ContractState;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;

import java.security.PublicKey;
import java.util.List;

public class HouseContract implements Contract {

    public static class Transfer implements CommandData {}
    public static class Register implements CommandData {}

    @Override
    public void verify(LedgerTransaction tx) throws IllegalArgumentException {
        if (tx.getCommands().size() != 1) {
            throw new IllegalArgumentException("Transaction must be one command.");
        }
        Command command = tx.getCommand(0);
        List<PublicKey> requiredSigners = command.getSigners();
        CommandData commandType = command.getValue();
        if (commandType instanceof Register) {
            //input/output state check
            if (tx.getInputs().size() != 0) {
                throw new IllegalArgumentException("Register no inputs.");
            }
            if (tx.getOutputs().size() != 1) {
                throw new IllegalArgumentException("Register only 1 output.");
            }

            //content
            ContractState outputState = tx.getOutput(0);
            if(!(outputState instanceof HouseState)) {
                throw new IllegalArgumentException("output state must be HouseState.");
            }
            HouseState houseState = (HouseState) outputState;
            if (houseState.getAddress().length() < 3) {
                throw new IllegalArgumentException("address length must be more than 3.");
            }
            if (houseState.getOwner().getName().getCountry().equals("B")) {
                throw new IllegalArgumentException("the country of owner cannot be B.");
            }

            //signer
            Party owner = houseState.getOwner();
            PublicKey ownersKey = owner.getOwningKey();
            if (!(requiredSigners.contains(ownersKey))) {
                throw new IllegalArgumentException("the owner must sign the reg.");
            }

        } else if (commandType instanceof Transfer) {
            //input/output state check
            if (tx.getInputs().size() != 1) {
                throw new IllegalArgumentException("Transfer only 1 input.");
            }
            if (tx.getOutputs().size() != 1) {
                throw new IllegalArgumentException("Transfer only 1 output.");
            }

            //content
            ContractState inputState = tx.getInput(0);
            ContractState outputState = tx.getOutput(0);

            if(!(inputState instanceof HouseState)) {
                throw new IllegalArgumentException("input state must be HouseState.");
            }
            if(!(outputState instanceof HouseState)) {
                throw new IllegalArgumentException("output state must be HouseState.");
            }

            HouseState inputHouseState = (HouseState) inputState;
            HouseState outputHouseState = (HouseState) outputState;

            if(!inputHouseState.getAddress().equals(outputHouseState.getAddress())) {
                throw new IllegalArgumentException("address cannot change.");
            }
            if(inputHouseState.getOwner().equals(outputHouseState.getOwner())) {
                throw new IllegalArgumentException("owner must change.");
            }

            //signer
            Party owner = inputHouseState.getOwner();
            PublicKey ownersKey = owner.getOwningKey();

            Party newOwner = outputHouseState.getOwner();
            PublicKey newOwnersKey = newOwner.getOwningKey();

            if (!(requiredSigners.contains(ownersKey))) {
                throw new IllegalArgumentException("the owner must sign the reg.");
            }
            if (!(requiredSigners.contains(newOwnersKey))) {
                throw new IllegalArgumentException("the owner must sign the reg.");
            }

        }
        throw new IllegalArgumentException("No support command.");
    }
}
