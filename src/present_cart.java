    import java.awt.*;
    import java.util.*;
    import java.io.*;


    import javax.swing.JFrame;
    import javax.swing.JPanel;

    public class present_cart extends JFrame{
        String fichier_seq = "";
        LinkedList list_point;
        JPanel jp= new JPanel();
        static String separateur= ",";
        static int NORMALISATION_VALEUR =20;
        JPanel jPanel1 = new Panel(this);
        static int LIMITE_VALEUR =-1;

        public present_cart(String lienFichier){
            //super();
            this.fichier_seq = lienFichier;
            this.setTitle("Fenêtre à tester");
            this.setSize(400, 400);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setContentPane(jPanel1);
            //this.add(jPanel1);
            //this.getContentPane().add(jPanel1);
            this.list_point = present_cart.charger_fichier_list(this.fichier_seq);
        }

        public void present_map(Graphics G){
            //Graphics G = jPanel1.getGraphics();
            if (list_point == null || list_point.size() < 2) {
                System.out.println("Point List Empty");
                return;
            }
            double [] bornes = calculerBornes(list_point);
            double minX = bornes[0], maxX = bornes[1], minY = bornes[2], maxY = bornes[3];

            int width = getWidth();
            int height = getHeight();

            int[] prevPoint = null;
            int segmentIndex = 1;

            for (int i = 0; i < list_point.size(); i++) {
                double[] currentPoint = (double[]) list_point.get(i);

                if (currentPoint[0] == present_cart.LIMITE_VALEUR) {
                    prevPoint = null;
                    continue;
                }

                int[] currentIntPoint = present_cart.nomaliser_table(currentPoint);
                int x = currentIntPoint[0];
                int y = currentIntPoint[1];

                G.fillOval(x - 3, y - 3, 6, 6);
                G.drawString("(" + currentIntPoint[0] + "," + currentIntPoint[1] + ")", x + 5, y - 5);

                if (prevPoint != null) {
                    G.drawLine(prevPoint[0], prevPoint[1], x, y);

                    G.setColor(Color.BLUE);
                    int midX = (prevPoint[0] + x) / 2;
                    int midY = (prevPoint[1] + y) / 2;
                    G.drawString(String.valueOf(segmentIndex), midX, midY);
                    G.setColor(Color.BLACK);

                    segmentIndex++;
                }

                prevPoint = currentIntPoint;
            }

            G.setColor(Color.DARK_GRAY);
            G.drawString("Total segments: " + cal_nbr_semg(), 10, 20);

        }

        public static double[] calculerBornes(LinkedList<double[]> list_point) {
            double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;

            for (double[] point : list_point) {
                if (point[0] == LIMITE_VALEUR) continue;

                if (point[0] < minX) minX = point[0];
                if (point[1] < minY) minY = point[1];
                if (point[0] > maxX) maxX = point[0];
                if (point[1] > maxY) maxY = point[1];
            }

            return new double[]{minX, maxX, minY, maxY};
        }

//        public static int[] normaliser_table(
//                double[] point,
//                double minX, double maxX,
//                double minY, double maxY,
//                int width, int height) {
//
//            int[] tab_int = new int[2];
//
//            // Avoid divide-by-zero errors
//            double rangeX = (maxX - minX == 0) ? 1 : (maxX - minX);
//            double rangeY = (maxY - minY == 0) ? 1 : (maxY - minY);
//
//            double xNorm = (point[0] - minX) / rangeX;
//            double yNorm = (point[1] - minY) / rangeY;
//
//            tab_int[0] = (int) (xNorm * (width - 40)) + 20;   // add padding
//            tab_int[1] = (int) (height - (yNorm * (height - 40)) - 20); // invert Y for display
//
//            return tab_int;
//        }

        public static int[] nomaliser_table(double[] table){
            int[] tab_int= new int[table.length];
            for(int i =0; i<table.length;i++){
                if(tab_int[i]!=present_cart.LIMITE_VALEUR)
                    tab_int[i]= (int)(table[i]*present_cart.NORMALISATION_VALEUR);
            }
            return tab_int;
        }

        public static LinkedList<double[]> charger_fichier_list(String url_fichier){
            LinkedList<double[]> list_point = new LinkedList<>();

            try{
                BufferedReader br= new BufferedReader (new FileReader(url_fichier));
                String line ="";

                while((line=br.readLine())!= null){
                    String[] tab_point_st= line.split(separateur);
                    double[] tab_point= new double[tab_point_st.length];
                    for(int i =0; i<tab_point.length;i++){
                        tab_point[i]=Double.parseDouble(tab_point_st[i].trim());
                    }
                    list_point.add(tab_point);
                }
            }

            catch(Exception e){System.out.println("il y a un problème de lexture du fichier text "+e.getMessage());}
            return list_point;
        }
        public static String lire_string(String m){
            System.out.println(m);
            String t=null;
            try{
                BufferedReader br= new BufferedReader(new InputStreamReader(System.in));
                t=br.readLine();
            }
            catch(Exception e){System.out.println(e.getMessage());}
            return t;
        }

        public static double lire_double(String m){
            double t = Double.parseDouble(present_cart.lire_string(m));
            return t;
        }

        public static void affich_list(LinkedList<double[]> list_point){
            System.out.println("affichage de la list des éléments du fichier");
            for (double[] coords : list_point) {
                System.out.println(Arrays.toString(coords));
            }
        }
        public int cal_nbr_semg(){
            int nbr_seg = 0;

            if (list_point == null || list_point.size() < 2) {
                System.out.println("Point List Empty");
                return 0;
            }

            for (int i = 0; i < list_point.size() - 1; i++) {
                double[] p_1 = (double[]) list_point.get(i);
                double[] p_2 = (double[]) list_point.get(i + 1);

                if (p_1 == null || p_2 == null || p_1.length < 2 || p_2.length < 2) continue;

                // Count only if both are valid points (not separator rows)
                if (p_1[0] != LIMITE_VALEUR && p_2[0] != LIMITE_VALEUR) {
                    nbr_seg++;
                }
            }

            return nbr_seg;
        }

        public static void main (String[] arg){
            String lienFichier = lire_string("Enter Text File Path: ");
            present_cart pc = new present_cart(lienFichier);
            System.out.println("Nomber de segments: " + pc.cal_nbr_semg());
            pc.affich_list(pc.list_point);
            pc.setVisible(true);
            pc.repaint();

            //LinkedList list_point= present_cart.charger_fichier_list(lire_string("uri_fichier"));
            //present_cart.affich_list(list_point);
        }
    }
