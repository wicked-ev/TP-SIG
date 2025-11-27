    import java.awt.*;
    import java.util.*;
    import java.io.*;
    import java.util.List;
    import javax.swing.JFrame;
    import javax.swing.JPanel;

    public class present_cart extends JFrame{
        String fichier_seq = "";
        LinkedList<double[]> list_point;
        JPanel jp= new JPanel();
        static String separateur= ",";
        static int NORMALISATION_VALEUR =10;
        JPanel jPanel1 = new Panel(this);
        static int LIMITE_VALEUR =-1;

        public present_cart(String lienFichier){
            //super();
            this.fichier_seq = lienFichier;
            this.setTitle("Fenêtre à tester");
            this.setSize(400,400);
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setContentPane(jPanel1);
            //this.add(jPanel1);
            //this.getContentPane().add(jPanel1);
            this.list_point = present_cart.charger_fichier_list(this.fichier_seq);
        }

        public List<double[]> getNodes() {
            List<Segment> segments = getSegments();
            Map<String, Set<String>> connections = new HashMap<>();

            for (Segment s : segments) {
                String p1Key = pointKey(s.p1);
                String p2Key = pointKey(s.p2);

                connections.putIfAbsent(p1Key, new HashSet<>());
                connections.putIfAbsent(p2Key, new HashSet<>());

                connections.get(p1Key).add(p2Key);
                connections.get(p2Key).add(p1Key);
            }

            List<double[]> nodes = new ArrayList<>();

            for (String key : connections.keySet()) {
                if (connections.get(key).size() >= 3) {
                    nodes.add(parsePointKey(key));
                }
            }

            return nodes;
        }

        private String pointKey(double[] p) {
            // Round to reduce floating point noise
            return String.format("%.6f,%.6f", p[0], p[1]);
        }

        private double[] parsePointKey(String key) {
            String[] parts = key.split(",");
            return new double[]{Double.parseDouble(parts[0]), Double.parseDouble(parts[1])};
        }


        public void present_map(Graphics G) {
            if (list_point == null || list_point.size() < 2) {
                System.out.println("Point List Empty");
                return;
            }

            for (double[] currentPoint : list_point) {
                if (currentPoint[0] == present_cart.LIMITE_VALEUR) continue;
                int[] currentIntPoint = present_cart.nomaliser_table(currentPoint);
                int x = currentIntPoint[0];
                int y = currentIntPoint[1];

                G.setColor(Color.BLACK);
                G.fillOval(x - 3, y - 3, 6, 6);
                G.drawString("(" + currentIntPoint[0] + "," + currentIntPoint[1] + ")", x + 5, y - 5);
            }

            List<Segment> segments = getSegments();

            Segment longest = null, shortest = null;
            for (Segment s : segments) {
                if (longest == null || s.length > longest.length) longest = s;
                if (shortest == null || s.length < shortest.length) shortest = s;
            }

            int segmentIndex = 1;
            for (Segment s : segments) {
                int[] p1 = nomaliser_table(s.p1);
                int[] p2 = nomaliser_table(s.p2);

                G.setColor(Color.BLACK);
                G.drawLine(p1[0], p1[1], p2[0], p2[1]);

                G.setColor(Color.BLUE);
                int midX = (p1[0] + p2[0]) / 2;
                int midY = (p1[1] + p2[1]) / 2;
                G.drawString(String.valueOf(segmentIndex), midX, midY);

                segmentIndex++;
            }

            G.setColor(Color.DARK_GRAY);
            G.drawString("Total unique segments: " + segments.size(), 10, 20);
            if (longest != null)
                G.drawString(String.format("Longest: %.2f", longest.length), 10, 35);
            if (shortest != null)
                G.drawString(String.format("Shortest: %.2f", shortest.length), 10, 50);
        }

        private boolean polygonsShareEdge(List<double[]> poly1, List<double[]> poly2) {
            for (int i = 0; i < poly1.size(); i++) {
                double[] a1 = poly1.get(i);
                double[] a2 = poly1.get((i + 1) % poly1.size());

                for (int j = 0; j < poly2.size(); j++) {
                    double[] b1 = poly2.get(j);
                    double[] b2 = poly2.get((j + 1) % poly2.size());
                    if ((samePoint(a1, b2) && samePoint(a2, b1)) ||
                            (samePoint(a1, b1) && samePoint(a2, b2))) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean samePoint(double[] p1, double[] p2) {
            return p1[0] == p2[0] && p1[1] == p2[1];
        }


        public List<Integer> getPolygonNeighbors(List<List<double[]>> polygons, int index) {
            List<Integer> neighbors = new ArrayList<>();
            List<double[]> target = polygons.get(index);

            for (int i = 0; i < polygons.size(); i++) {
                if (i == index) continue;
                if (polygonsShareEdge(target, polygons.get(i))) {
                    neighbors.add(i);
                }
            }

            return neighbors;
        }

        public static int[] nomaliser_table(double[] table){
            int[] tab_int= new int[table.length];
            for(int i =0; i<table.length;i++){
                if(tab_int[i]!=present_cart.LIMITE_VALEUR)
                    tab_int[i]= (int)(Math.abs(table[i] * present_cart.NORMALISATION_VALEUR));
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
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
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
//            int nbr_seg = 0;
//
//            if (list_point == null || list_point.size() < 2) {
//                System.out.println("Point List Empty");
//                return 0;
//            }
//
//            for (int i = 0; i < list_point.size() - 1; i++) {
//                double[] p_1 = (double[]) list_point.get(i);
//                double[] p_2 = (double[]) list_point.get(i + 1);
//
//                if (p_1 == null || p_2 == null || p_1.length < 2 || p_2.length < 2) continue;
//
//                // Count only if both are valid points (not separator rows)
//                if (p_1[0] != LIMITE_VALEUR && p_2[0] != LIMITE_VALEUR) {
//                    nbr_seg++;
//                }
//            }
            return getSegments().size();
        }

        public int calc_global_para() {
            int paramater = 0;
            List<Segment> segments = getSegments();

            for (Segment segment : segments) {
                paramater += segment.length;
            }
            return  paramater;
        }

        public List<Segment> getSegments() {
            List<Segment> segments = new ArrayList<>();

            if (list_point == null || list_point.size() < 2) return segments;

            for (int i = 0; i < list_point.size() - 1; i++) {
                double[] p1 = (double[]) list_point.get(i);
                double[] p2 = (double[]) list_point.get(i + 1);

                if (p1 == null || p2 == null) continue;
                if (p1[0] == LIMITE_VALEUR || p2[0] == LIMITE_VALEUR) continue;

                boolean alreadySeg = false;

                for (Segment s : segments) {

                    if ((samePoint(s.p1, p1) && samePoint(s.p2, p2)) ||
                            (samePoint(s.p1, p2) && samePoint(s.p2, p1))) {
                        alreadySeg = true;
                        break;
                    }
                }

                if (!alreadySeg) {
                    segments.add(new Segment(p1, p2));
                }
            }
            return segments;
        }

        public LinkedList<double[]> create_ficher_node() {
            LinkedList<double[]> list = pointsSum();
//            printDoubleList(list, "please work");

            LinkedList<double[]> dim = new LinkedList<>();
            for(int i = 0; i < list.size(); i++) {
                double[] p = list.get(i);
                double[] node = {i, p[0], p[1]};
                dim.add(node);
            }
            printDoubleList(dim,"dim list");
            return dim;
        }

        public double[] getNode(double[] point) {
            LinkedList<double[]> dim = create_ficher_node();

            for (double[] dimPoint : dim) {
                double[] p1 = {dimPoint[1], dimPoint[2]};
                if (samePoint(point, p1)) {
                    return dimPoint;
                }
            }
            return null;
        }

        // sege point
        public LinkedList<Zone> get_zones() {
            LinkedList<Zone> zones = new LinkedList<>();
            int zoneId = 0;
            Zone currentZone = new Zone(zoneId);
            zones.add(currentZone);

            for (int i = 0; i < this.list_point.size(); i++) {
                double[] p = list_point.get(i);

                if (p[0] == -1 && p[1] == -1) {
                    if (i + 1 < list_point.size()) {
                        zoneId++;
                        currentZone = new Zone(zoneId);
                        zones.add(currentZone);
                    }
                    continue;
                }

                currentZone.addPoint(p);
            }
            return zones;
        }



        public Zone[] getZoneSides(double[] point1, double[] point2) {

            LinkedList<Zone> zones = get_zones();
            for (Zone zone : zones) {
                System.out.println("zone id" + zone.id);
                printDoubleList(zone.points, "zone " + zone.id);
            }

            Zone[] adjen_zone = new Zone[2];
            int count = 0;
            for (Zone zone : zones) {
                for (int i = 0; i < zone.points.size(); i++) {
                    double[] p1 = zone.points.get(i);
                    double[] p2 = null;
                    if (i+1 < zone.points.size()) {
                        p2 = zone.points.get((i + 1) % zone.points.size());
                    }
                    if (p2 == null) continue;

                    if((samePoint(point1, p1) && samePoint(point2, p2))
                            || (samePoint(point1, p2) && samePoint(point2, p1))) {
                        adjen_zone[count++] = zone;
                        if (count == 2) {
                            return adjen_zone;
                        }
                        break;
                    }
                }
            }
            return adjen_zone;
        }

        public double[] calc_centeriod(Zone zone) {
            double x = 0, y = 0;
            for(double[] point : zone.points) {
                x+= point[0];
                y+= point[1];
            }
            int n = zone.points.size();
            return new double[]{x/n, y/n};
        }
        public double[] getLeftRightZones(double[] p1, double[] p2) {
            Zone[] adj_zone = getZoneSides(p1,p2);
            System.out.println("len " + adj_zone.length);

            double left = -1;
            double right = -1;

            for(Zone zone : adj_zone) {
                if(zone == null) continue;
                double[] c = calc_centeriod(zone);

                double cross = (p2[0] - p1[0]) * (c[1] - p1[1])
                        - (p2[1] - p1[1]) * (c[0] - p1[0]);

                if(cross > 0) {
                    left = zone.id;
                } else if (cross < 0) {
                    right = zone.id;
                }
            }

            return new double[] {left, right};
        }
        public LinkedList<double[]> create_arret_ficher() {
            List<Segment> segments = getSegments();
            LinkedList<double[]> arret_list = new LinkedList<>();
            int idIndex = 0;
            for(Segment s : segments) {

                var node1 = getNode(s.p1);
                var node2 = getNode(s.p2);
                double[] zones = getLeftRightZones(s.p1, s.p2);
                double[] arret = {idIndex, node1[0], node2[0], zones[0], zones[1] };
                arret_list.add(arret);
                idIndex++;
            }
            return arret_list;
        }
        public double clalc_perimeter() {

            List<Segment> segments = getSegments();
            double perimeter = 0.0;

            for (int i = 0; i < segments.size(); i++) {
                Segment s1 = segments.get(i);
                boolean shared = false;

                for (int j = 0; j < segments.size(); j++) {
                    if (i == j) continue;
                    Segment s2 = segments.get(j);

                    if ((samePoint(s1.p1, s2.p1) && samePoint(s1.p2, s2.p2)) ||
                             (samePoint(s1.p1, s2.p2) && samePoint(s1.p2, s2.p1))) {
                        shared = true;
                        break;
                    }
                }

                if (!shared) {
                    perimeter += s1.length;
                }
            }

            return perimeter;

        }

        public void printDoubleList(LinkedList<double[]> list, String title) {
            System.out.println("===== " + title + " =====");
            int index = 0;
            for (double[] arr : list) {
                System.out.print("[" + index + "] ");
                for (int i = 0; i < arr.length; i++) {
                    System.out.print(arr[i]);
                    if (i < arr.length - 1) System.out.print(", ");
                }
                System.out.println();
                index++;
            }
            System.out.println("======================\n");
        }
        public void printArrets(LinkedList<double[]> arret_list) {
            System.out.println("ID | Node1 | Node2 | LeftZone | RightZone");
            System.out.println("------------------------------------------");

            for (double[] a : arret_list) {
                System.out.printf(
                        "%2d | %5d | %5d | %9d | %9d%n",
                        (int)a[0], (int)a[1], (int)a[2], (int)a[3], (int)a[4]
                );
            }
        }


        public LinkedList<double[]> pointsSum() {
            LinkedList<double[]> filtredList = new LinkedList<>();

            for (int i = 0; i < list_point.size(); i++) {
                double[] p1 = list_point.get(i);

                if (p1 == null || p1[0] == LIMITE_VALEUR) continue;

                boolean isUnique = true;

                for (int j = 0; j < i; j++) {
                    double[] p2 = list_point.get(j);
                    if (p2 == null || p2[0] == LIMITE_VALEUR) continue;

                    if (samePoint(p1, p2)) {
                        isUnique = false;
                        break;
                    }
                }

                if (isUnique) {
                    filtredList.add(p1);
                }
            }

            return filtredList;
        }


        public static void main (String[] arg){
            //String lienFichier = lire_string("Enter Text File Path: ");
            String lienFichier = "C:/Users/admin/Downloads/testGherbaoui.txt";
            present_cart pc = new present_cart(lienFichier);

            pc.affich_list(pc.list_point);

            System.out.println("segments: " + pc.getSegments().size());
            System.out.println("points counts: " + pc.list_point.size());
            LinkedList<double[]> arretlist = pc.create_arret_ficher();
            pc.printArrets(arretlist);
            System.out.println("parameter: " + pc.calc_global_para());
            System.out.println("parameter: " + pc.clalc_perimeter());

            pc.setVisible(true);
            pc.repaint();
        }
    }
