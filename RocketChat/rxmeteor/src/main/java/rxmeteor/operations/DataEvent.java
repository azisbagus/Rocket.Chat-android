package rxmeteor.operations;

/**
 * Created by julio on 05/12/15.
 */
public class DataEvent {
    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public enum DataType {
        ADD, CHANGE, REMOVE
    }

    private DataType type;
    String collectionName;
    String documentID;
    String newValuesJson;
    String removedValuesJson;

    public DataEvent(DataType type, String collectionName, String documentID, String newValuesJson, String removedValuesJson) {
        this.type = type;
        this.collectionName = collectionName;
        this.documentID = documentID;
        this.newValuesJson = newValuesJson;
        this.removedValuesJson = removedValuesJson;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public String getNewValuesJson() {
        return newValuesJson;
    }

    public void setNewValuesJson(String newValuesJson) {
        this.newValuesJson = newValuesJson;
    }

    public String getRemovedValuesJson() {
        return removedValuesJson;
    }

    public void setRemovedValuesJson(String removedValuesJson) {
        this.removedValuesJson = removedValuesJson;
    }
}
