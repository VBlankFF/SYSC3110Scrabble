import java.io.*;

/**
 * Class to help handle game serialization and deserialization.
 *
 * @author Esli Emmanuel Konate 101322259
 * version 1.0
 */


public class GameSerializer {
    private static final String SAVE = "saved_games";
    private static final String FILE_EXTENSION = ".scrabble";

    /**
     * Saves a ScrabbleModel to a file
     * @param model the model to save
     * @param gameName the name for the saved game
     * @return true if we succeded and false if not
     */
    public static boolean saveGame(ScrabbleModel model, String gameName){
        if (model == null || gameName == null || gameName.trim().isEmpty()){
            return false;
        }

        File saveDir = new File(SAVE);
        if (!saveDir.exists()){
            boolean created = saveDir.mkdirs();
            if (!created){
                System.err.println("We have failed to create directory: " + saveDir.getAbsolutePath());
                return false;
            }
        }

        String filename = SAVE + File.separator + gameName + FILE_EXTENSION;

        try (ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filename)))){
            out.writeObject(model);
            return true;
        } catch (IOException e){
            System.err.println("We have failed to save the game state: " + e.getMessage());
            return false;
        }
    }

    /**
     * This loads a ScrabbleModel from a file
     * @param gameName the name of the saved game.
     * @return the loaded model, or null if loading has failed.
     */
    public static ScrabbleModel loadGame(String gameName){
        if (gameName == null || gameName.trim().isEmpty()){
            return null;
        }
        String filename = SAVE + File.separator + gameName + FILE_EXTENSION;

        try (ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filename)))){
            Object obj = in.readObject();
            if (obj instanceof  ScrabbleModel){
                return (ScrabbleModel) obj;
            } else {
                System.err.println("This is an invalid file format to save");
                return null;
            }
        } catch (IOException e){
            System.err.println("Error when reading the save file: " + e.getMessage());
            return null;
        } catch (ClassNotFoundException e){
            System.err.println("Save file does not contain valid data: " + e.getMessage());
            return null;
        }
    }

    /**
     * This deletes a saved game
     * @param gameName the name of the game to delete
     * @return true if successful and false if oherwise
     */
    public static boolean deleteSavedGame(String gameName){
        if (gameName == null || gameName.trim().isEmpty()){
            return false;
        }

        String filename = SAVE + File.separator + gameName + FILE_EXTENSION;
        File file = new File(filename);

        return file.exists() && file.delete();
    }

}

