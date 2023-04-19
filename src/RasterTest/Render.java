package RasterTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Render {


    private List<Triangle> renderedTriangles;


    /**
       getter
       @return unmodified list of rendered triangles
     */
    public List<Triangle> getRenderedTriangles() {
        renderedTriangles = new ArrayList<>(List.of(new Triangle(100, 100, 200, 100 ,100, 200), new Triangle(200, 200, 300, 200 ,200, 300)));
        return Collections.unmodifiableList(renderedTriangles);
    }
}
