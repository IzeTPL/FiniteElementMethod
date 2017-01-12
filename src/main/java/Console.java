import fem.transientsolution.TransientSolutionGrid;

import java.io.File;

/**
 * Created by Marian on 06.01.2017.
 */
public class Console {

    public static final ClassLoader loader = Console.class.getClassLoader();

    public static void main(String[] args) throws Exception{

        String str = Main.loader.getResource("data/2/zadanie.json").toString();
        str = str.substring(6);

        File file = new File(str);

        TransientSolutionGrid grid = new TransientSolutionGrid(file);

    }

}
