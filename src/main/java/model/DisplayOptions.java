package model;

public enum DisplayOptions {
    ENABLE_SUBSTEPS {
        public String toString() {
            return "Enable substeps";
        }
    },
    GRAPH_NODES {
        public String toString() {
            return "Graph nodes";
        }
    },
    GRAPH_EDGES {
        public String toString() {
            return "Graph edges";
        }
    },
    DELAUNAY_CIRCLES {
        public String toString() {
            return "Delaunay circles";
        }
    },
    DELAUNAY_EDGES {
        public String toString() {
            return "Delaunay edges";
        }
    },
    VORONOI_EDGES {
        public String toString() {
            return "Voronoi edges";
        }
    },
    VORONOI_NODES {
        public String toString() {
            return "Voronoi nodes";
        }
    },
    VORONOI_CENTROIDS {
        public String toString() {
            return "Voronoi centroids";
        }
    },
    SHOW_NODE_DISPLACEMENT {
        public String toString() {
            return "Show node displacement";
        }
    },
}
