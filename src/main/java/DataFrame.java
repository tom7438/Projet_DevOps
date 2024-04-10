import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedHashMap;

public class DataFrame {
    private final List<List<Object>> data;
    private final LinkedHashMap<String, Class<?>> columns;

    public DataFrame(List<List<Object>> data, LinkedHashMap<String, Class<?>> columns) {
        this.data = data;
        this.columns = columns;
    }

    public DataFrame(Object[][] data, LinkedHashMap<String, Class<?>> columns) {
        this.data = new ArrayList<>();
        for (Object[] row : data) {
            List<Object> rowData = new ArrayList<>(Arrays.asList(row));
            this.data.add(rowData);
        }
        this.columns = columns;
    }

    public List<List<Object>> getData() {
        return data;
    }

    public LinkedHashMap<String, Class<?>> getColumns() {
        return columns;
    }

    public int getIndexesColone(String nomColonne) {
        int index = 0;
        for (String columnName : columns.keySet()) {
            if (columnName.equals(nomColonne)) {
                return index;
            }
            index++;
        }
        throw new IllegalArgumentException("Le nom de la colonne est invalide");
    }

    // Méthode pour ajouter une ligne au DataFrame
    public void ajouterLigne(List<Object> ligne) {
        // Validation des données d'entrée
        if (ligne == null || ligne.size() != columns.size()) {
            throw new IllegalArgumentException("La ligne est invalide");
        }
        data.add(new ArrayList<>(ligne));
    }

    // Méthode pour supprimer une ligne du DataFrame
    public void supprimerLigne(int indexLigne) {
        if (indexLigne < 0 || indexLigne >= data.size()) {
            throw new IllegalArgumentException("L'index de la ligne à supprimer est invalide");
        }
        data.remove(indexLigne);
    }

    // Méthode pour supprimer une colonne du DataFrame
    public void supprimerColonne(String nomColonne) {
        if (!columns.containsKey(nomColonne)) {
            throw new IllegalArgumentException("Le nom de la colonne à supprimer est invalide");
        }
        int indexColonne = getIndexesColone(nomColonne);
        columns.remove(nomColonne);
        for (List<Object> rowData : data) {
            rowData.remove(indexColonne);
        }
    }

    // Méthode pour obtenir une valeur du DataFrame
    public Object obtenirValeur(int indexLigne, String nomColonne) {
        if (indexLigne < 0 || indexLigne >= data.size()) {
            throw new IllegalArgumentException("L'index de la ligne à obtenir est invalide");
        }
        if (!columns.containsKey(nomColonne)) {
            throw new IllegalArgumentException("Le nom de la colonne à obtenir est invalide");
        }
        int indexColonne = getIndexesColone(nomColonne);
        return data.get(indexLigne).get(indexColonne);
    }

    public DataFrame select_line(int[] indexes) {
        List<List<Object>> new_data = new ArrayList<>();
        for (int i = 0; i < indexes.length; i++) {
            new_data.add(data.get(indexes[i]));
        }
        return new DataFrame(new_data, columns);
    }

    public DataFrame select_column(String[] column_names) {
        int[] indexes = new int[column_names.length];
        List<List<Object>> new_data = new ArrayList<>();
        LinkedHashMap<String, Class<?>> new_columns = new LinkedHashMap<>();
        for (int i = 0; i < column_names.length; i++) {
            indexes[i] = -1;
            for (int j = 0; j < data.get(0).size(); j++) {
                if (column_names[i].equals(columns.keySet().toArray()[j])) {
                    indexes[i] = j;
                    new_columns.put(column_names[i], columns.get(column_names[i]));
                    break;
                }
            }
            if (indexes[i] == -1) {
                throw new IllegalArgumentException("Column not found");
            }
        }
        for (List<Object> line : data) {
            List<Object> new_line = new ArrayList<>();
            for (int index : indexes) {
                new_line.add(line.get(index));
            }
            new_data.add(new_line);
        }

        return new DataFrame(new_data, new_columns);
    }

    public void display() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        // Trouver la largeur maximale de chaque colonne
        int[] maxWidths = getMaxWidth();

        StringBuilder sb = new StringBuilder();

        // En-têtes
        sb.append(String.format("%4s", "")).append("\t"); // En-tête vide pour la première colonne
        int index = 0;
        for (String key : columns.keySet()) {
            sb.append(String.format("%" + (maxWidths[index++] + 1) + "s", key)).append("\t");
        }
        sb.append("\n");

        // Données
        int i = 0;
        for (List<Object> row : data) {
            sb.append(String.format("%4d", i++)).append("\t"); // Index des lignes
            int j = 0;
            for (Object obj : row) {
                sb.append(String.format("%" + (maxWidths[j++] + 1) + "s", obj)).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    public void displayFirstLines(int n) {
        if (n > data.size()) {
            System.out.println("DataFrame a moins de " + n + " lignes");
            n = data.size();
        }
        // Trouver la largeur maximale de chaque colonne
        int[] maxWidths = getMaxWidth();

        System.out.println("Voici les " + n + " premières lignes:");

        StringBuilder sb = getEnTete();

        // Données
        int i = 0;
        for (List<Object> row : data) {
            sb.append(String.format("%4d", i++)).append("\t"); // Index des lignes
            int j = 0;
            for (Object obj : row) {
                sb.append(String.format("%" + (maxWidths[j++] + 1) + "s", obj)).append("\t");
            }
            sb.append("\n");
            if (i == n) {
                break;
            }
        }
        System.out.println(sb.toString());
    }

    public void displayLastLines(int n) {
        if(n > data.size()) {
            System.out.println("DataFrame a moins de " + n + " lignes");
            n = data.size();
        }
        // Trouver la largeur maximale de chaque colonne
        int[] maxWidths = getMaxWidth();

        System.out.println("Voici les " + n + " dernières lignes:");

        StringBuilder sb = getEnTete();

        // Données
        int i = 0;
        for (int j = data.size() - n; j < data.size(); j++) {
            List<Object> row = data.get(j);
            sb.append(String.format("%4d", i++)).append("\t"); // Index des lignes
            int k = 0;
            for (Object obj : row) {
                sb.append(String.format("%" + (maxWidths[k++] + 1) + "s", obj)).append("\t");
            }
            sb.append("\n");
        }
        System.out.println(sb.toString());
    }

    private int[] getMaxWidth() {
        int[] maxWidths = new int[columns.size()];
        for (List<Object> row : data) {
            for (int i = 0; i < row.size(); i++) {
                maxWidths[i] = Math.max(maxWidths[i], String.valueOf(row.get(i)).length());
            }
        }
        return maxWidths;
    }

    public StringBuilder getEnTete() {
        int[] maxWidths = getMaxWidth();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%4s", "")).append("\t"); // En-tête vide pour la première colonne
        int index = 0;
        for (String key : columns.keySet()) {
            sb.append(String.format("%" + (maxWidths[index++] + 1) + "s", key)).append("\t");
        }
        sb.append("\n");
        return sb;
    }
}
