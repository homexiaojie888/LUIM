import java.util.ArrayList;
import java.util.List;

public class CandidateGenerator {
    List<Integer> CandidateItemset=new ArrayList<>();
    boolean isGenerator;
    int pre_TWU;
    int TWU;

    public List<Integer> getCandidateItemset() {
        return CandidateItemset;
    }

    public void setCandidateItemset(List<Integer> candidateItemset) {
        CandidateItemset = candidateItemset;
    }

    public boolean isGenerator() {
        return isGenerator;
    }

    public void setIsGenerator(boolean isGenerator) {
        this.isGenerator = isGenerator;
    }

    public int getPre_TWU() {
        return pre_TWU;
    }

    public void setPre_TWU(int pre_TWU) {
        this.pre_TWU = pre_TWU;
    }

    public int getTWU() {
        return TWU;
    }

    public void setTWU(int TWU) {
        this.TWU = TWU;
    }
}

