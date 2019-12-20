package gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import map.elements.animal.FollowedAnimal;



public class FollowedAnimalGrid extends GridPane {
    FollowedAnimal followedAnimal;
    CanvasMap canvasMap;
    Label countInfo;
    Label genomeDesc;
    Label genome;
    Label childrenDesc;
    Label childrenTillNow;
    Label descendandsDesc;
    Label descendandsTillNow;
    Label timeOfDeath;




    public FollowedAnimalGrid(CanvasMap canvasMap){
        super();
        this.canvasMap = canvasMap;
        this.followedAnimal = canvasMap.map.statistics.animalFollowed;
        this.setVgap(10);

        countInfo = new Label("Obserwujesz to zwierzę od 0 epok.");
        genomeDesc = new Label("Genotyp: ");
        genome = new Label();
        childrenDesc = new Label("Liczba dzieci: ");
        descendandsDesc = new Label("Liczba potomków: ");
        childrenTillNow = new Label("0");
        descendandsTillNow = new Label("0");
        timeOfDeath = new Label();

        genomeDesc.setAlignment(Pos.CENTER);


        this.setConstraints(countInfo,0,0,2,1);
        this.setConstraints(genomeDesc,0,1,2,1);
        this.setConstraints(genome,0,2,2,1);
        this.setConstraints(childrenDesc,0,3);
        this.setConstraints(childrenTillNow,1,3);
        this.setConstraints(descendandsDesc,0,4);
        this.setConstraints(descendandsTillNow,1,4);
        this.setConstraints(descendandsTillNow,1,4);
        this.setConstraints(timeOfDeath,0,5,2,1);


        this.getChildren().addAll(countInfo,genomeDesc,genome,childrenDesc,childrenTillNow,descendandsDesc,descendandsTillNow);
    }

    public void updateStatistics(){
        this.followedAnimal = canvasMap.map.statistics.animalFollowed;
        if(followedAnimal != null){
            genome.setText(followedAnimal.animal.getGenoType().toString());
            if(!followedAnimal.isDead()){
                followedAnimal.timeGoesBy();
                countInfo.setText("Obserwujesz to zwierzę od " + followedAnimal.countRounds() + " epok.");
                childrenTillNow.setText(Integer.toString(followedAnimal.countChildren()));
                descendandsTillNow.setText(Long.toString(followedAnimal.countDescendands()));
            }
            else{
                timeOfDeath.setText("Śmierć nastąpiła w " + followedAnimal.getTimeOfDeath() + ". rundzie");
                if(!this.getChildren().contains(timeOfDeath))
                    this.getChildren().add(timeOfDeath);
            }
        }
    }


}
