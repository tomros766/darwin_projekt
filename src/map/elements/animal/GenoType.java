package map.elements.animal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GenoType {
    final ArrayList<Integer> genes;

    public GenoType(){
        this.genes = new ArrayList<>();
        for(int i = 0; i < 32; i++){
            this.genes.add(new Random().nextInt(8));
        }
        ensureAllGenes(genes);
        this.genes.sort(Integer::compareTo);
    }

    private void ensureAllGenes(ArrayList<Integer> genes) {
        List<Integer> allTypes = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7);
        while (!genes.containsAll(allTypes)) {
            for (int g : allTypes) {
                if (!genes.contains(g)) {
                    genes.set(new Random().nextInt(32), g);
                }
            }
        }
    }

    public GenoType(GenoType par1, GenoType par2){
        this.genes = prepareGenes(par1, par2);

        if (genes.size() != 32) throw new IllegalArgumentException("Given genotype is invalid! Wrong number of genes " + genes.size());
    }

    private ArrayList<Integer> prepareGenes(GenoType dominant, GenoType subservient) {
        ArrayList<Integer> genes = new ArrayList<>();
        genes.clear();

        int[] cuttingPoints = generateCutPoints();


        genes.addAll(dominant.genes.subList(0,cuttingPoints[0]));
        genes.addAll(cuttingPoints[0], subservient.genes.subList(cuttingPoints[0],cuttingPoints[1]));
        genes.addAll(cuttingPoints[1], dominant.genes.subList(cuttingPoints[1], 32));


        ensureAllGenes(genes);
        return genes;
    }

    private int[] generateCutPoints() {
        int cut1, cut2;
        do{
            cut1 = new Random().nextInt(32);
            cut2 = new Random().nextInt(32);
        }
        while(cut1 == cut2);

        if(cut1 > cut2){
            int tmp = cut1;
            cut1 = cut2;
            cut2 = tmp;
        }

        return new int[] {cut1, cut2};
    }



}
