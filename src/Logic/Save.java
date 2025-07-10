package Logic;

import java.io.Serializable;

public class Save implements Serializable, Comparable<Save> {

    private final String SAVE_NAME;
    private final int RESULT;

    public Save(String saveName, int result) {
        this.SAVE_NAME = saveName;
        this.RESULT = result;
    }

    public int getResult() {
        return this.RESULT;
    }

    public String getSaveName() {
        return this.SAVE_NAME;
    }

    @Override
    public int compareTo(Save other) {
        return Integer.compare(other.RESULT, this.RESULT);
    }

    @Override
    public String toString() {
        return this.SAVE_NAME + " | " + this.RESULT;
    }
}

