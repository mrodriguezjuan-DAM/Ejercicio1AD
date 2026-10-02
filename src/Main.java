import java.util.Scanner;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Main {

    public static void main(String[] Args) {

        List<Pelicula> listaPeliculas = leerPeliculas("datos/peliculas.txt");
        Scanner sc = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n===== CATÁLOGO =====");
            System.out.println("1. Mostrar todas");
            System.out.println("2. Buscar por género");
            System.out.println("3. Buscar por título");
            System.out.println("4. Exportar a XML");
            System.out.println("5. Importar XML");
            System.out.println("0. Salir");
            System.out.print("Elige una opción: ");

            opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    for (Pelicula p : listaPeliculas) {
                        System.out.println(p.getNombre());
                    }
                    break;

                case 2:
                    List<String> generos = new ArrayList<>();
                    for (Pelicula p : listaPeliculas) {
                        if (!generos.contains(p.getGenero())) {
                            generos.add(p.getGenero());
                        }
                    }

                    for (int i = 0; i < generos.size(); i++) {
                        System.out.println((i + 1) + ". " + generos.get(i));
                    }

                    System.out.print("Selecciona un género (número o nombre): ");
                    String entradaGenero = sc.nextLine().trim();
                    String generoSeleccionado = entradaGenero;

                    if (entradaGenero.matches("\\d+")) {
                        int numGenero = Integer.parseInt(entradaGenero);
                        if (numGenero >= 1 && numGenero <= generos.size()) {
                            generoSeleccionado = generos.get(numGenero - 1);
                        }
                    }

                    for (Pelicula p : listaPeliculas) {
                        if (p.getGenero().equalsIgnoreCase(generoSeleccionado)) {
                            System.out.println(p);
                        }
                    }
                    break;

                case 3:
                    System.out.print("Introduce el título a buscar: ");
                    String texto = sc.nextLine().toLowerCase();

                    for (Pelicula p : listaPeliculas) {
                        if (p.getNombre().toLowerCase().contains(texto)) {
                            System.out.println(p);
                        }
                    }
                    break;

                case 4:
                    exportarXML(listaPeliculas, "datos/peliculas.xml");
                    break;

                case 5:
                    List<Pelicula> peliculasXML = importarXML("datos/peliculas.xml");
                    for (Pelicula p : peliculasXML) {
                        System.out.println(p);
                    }
                    break;

                case 0:
                    System.out.println("Fin del programa.");
                    break;

                default:
                    System.out.println("Opción no válida.");
                    break;
            }
        }
    }

    public static List<Pelicula> leerPeliculas(String nombreFichero) {
        List<Pelicula> lista = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(nombreFichero))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.replace("\uFEFF", "").trim();

                if (!linea.isEmpty()) {
                    String[] datos = linea.split("\\|");
                    int id = Integer.parseInt(datos[0].replaceAll("[^0-9]", ""));
                    String nombre = datos[1].trim();
                    String autor = datos[2].trim();
                    int anyo = Integer.parseInt(datos[3].replaceAll("[^0-9]", ""));
                    String genero = datos[4].trim();

                    Pelicula p = new Pelicula(id, nombre, autor, anyo, genero);
                    lista.add(p);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el fichero: " + e.getMessage());
        }

        return lista;
    }

    public static void exportarXML(List<Pelicula> peliculas, String nombreFichero) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element raiz = doc.createElement("peliculas");
            doc.appendChild(raiz);

            for (Pelicula p : peliculas) {
                Element elemPelicula = doc.createElement("pelicula");
                elemPelicula.setAttribute("id", String.valueOf(p.getId()));

                Element elemNombre = doc.createElement("nombre");
                elemNombre.appendChild(doc.createTextNode(p.getNombre()));
                elemPelicula.appendChild(elemNombre);

                Element elemAutor = doc.createElement("autor");
                elemAutor.appendChild(doc.createTextNode(p.getAutor()));
                elemPelicula.appendChild(elemAutor);

                Element elemAnyo = doc.createElement("anyo");
                elemAnyo.appendChild(doc.createTextNode(String.valueOf(p.getAnyo())));
                elemPelicula.appendChild(elemAnyo);

                Element elemGenero = doc.createElement("genero");
                elemGenero.appendChild(doc.createTextNode(p.getGenero()));
                elemPelicula.appendChild(elemGenero);

                raiz.appendChild(elemPelicula);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(nombreFichero));
            transformer.transform(source, result);

            System.out.println("Archivo XML exportado correctamente en " + nombreFichero);

        } catch (Exception e) {
            System.out.println("Error al exportar a XML: " + e.getMessage());
        }
    }

    public static List<Pelicula> importarXML(String nombreFichero) {
        List<Pelicula> lista = new ArrayList<>();

        try {
            File archivo = new File(nombreFichero);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(archivo);
            doc.getDocumentElement().normalize();

            NodeList listaNodos = doc.getElementsByTagName("pelicula");

            for (int i = 0; i < listaNodos.getLength(); i++) {
                Node nodo = listaNodos.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elemento = (Element) nodo;

                    int id = Integer.parseInt(elemento.getAttribute("id"));
                    String nombre = elemento.getElementsByTagName("nombre").item(0).getTextContent();
                    String autor = elemento.getElementsByTagName("autor").item(0).getTextContent();
                    int anyo = Integer.parseInt(elemento.getElementsByTagName("anyo").item(0).getTextContent());
                    String genero = elemento.getElementsByTagName("genero").item(0).getTextContent();

                    Pelicula p = new Pelicula(id, nombre, autor, anyo, genero);
                    lista.add(p);
                }
            }
        } catch (Exception e) {
            System.out.println("Error al importar el XML: " + e.getMessage());
        }

        return lista;
    }
}