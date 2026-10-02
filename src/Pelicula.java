public class Pelicula {

    private int id;
    private String nombre;
    private String autor;
    private int anyo;
    private String genero;

    public Pelicula(int id, String nombre, String autor, int anyo, String genero) {
        this.id = id;
        this.nombre = nombre;
        this.autor = autor;
        this.anyo = anyo;
        this.genero = genero;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getAutor() {
        return autor;
    }

    public int getAnyo() {
        return anyo;
    }

    public String getGenero() {
        return genero;
    }

    @Override
    public String toString() {
        return id + " - " + nombre + " (" + anyo + ") | Director: " + autor + " | Género: " + genero;
    }
}