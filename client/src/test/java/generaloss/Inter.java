package generaloss;

import jpize.util.Rect;
import jpize.util.array.FloatList;
import jpize.util.math.Mathc;
import jpize.util.math.vector.Vec2f;

import java.util.*;

public class Inter {

    public static int getPointsOrientation(float ax, float ay, float bx, float by, float cx, float cy) {
        return Mathc.signum((by - ay) * (cx - bx) - (bx - ax) * (cy - by));
    }

    public static boolean getLineIntersectLine(Vec2f dst, float a1, float b1, float c1, float a2, float b2, float c2) {
        final float determinant = (a1 * b2 - a2 * b1);
        if(determinant == 0F)
            return false;

        if(dst != null)
            dst.set((b2 * c1 - b1 * c2), (a1 * c2 - a2 * c1)).div(determinant);

        return true;
    }

    public static boolean getSegmentIntersectSegment(Vec2f dst,
                                                     float beginX1, float beginY1, float endX1, float endY1,
                                                     float beginX2, float beginY2, float endX2, float endY2) {

        final int o1 = getPointsOrientation(beginX1, beginY1, endX1, endY1, beginX2, beginY2);
        final int o2 = getPointsOrientation(beginX1, beginY1, endX1, endY1, endX2, endY2);
        final int o3 = getPointsOrientation(beginX2, beginY2, endX2, endY2, beginX1, beginY1);
        final int o4 = getPointsOrientation(beginX2, beginY2, endX2, endY2, endX1, endY1);

        // general case
        if(o1 != o2 && o3 != o4) {
            final float a1 = (endY1 - beginY1);
            final float b1 = (beginX1 - endX1);
            final float c1 = (a1 * beginX1 + b1 * beginY1);

            final float a2 = (endY2 - beginY2);
            final float b2 = (beginX2 - endX2);
            final float c2 = (a2 * beginX2 + b2 * beginY2);

            return getLineIntersectLine(dst, a1, b1, c1, a2, b2, c2);
        }

        return false;
    }

    public static boolean getSegmentIntersectSegment(Vec2f dst, Vec2f begin1, Vec2f end1, Vec2f begin2, Vec2f end2) {
        return getSegmentIntersectSegment(dst, begin1.x, begin1.y, end1.x, end1.y, begin2.x, begin2.y, end2.x, end2.y);
    }

    public static boolean isLineIntersectLine(float a1, float b1, float c1, float a2, float b2, float c2) {
        final float determinant = (a1 * b2 - a2 * b1);
        return (determinant != 0F);
    }

    public static boolean isSegmentIntersectSegment(float beginX1, float beginY1, float endX1, float endY1,
                                                    float beginX2, float beginY2, float endX2, float endY2) {

        final int o1 = getPointsOrientation(beginX1, beginY1, endX1, endY1,  beginX2, beginY2);
        final int o2 = getPointsOrientation(beginX1, beginY1, endX1, endY1,  endX2, endY2);
        final int o3 = getPointsOrientation(beginX2, beginY2, endX2, endY2,  beginX1, beginY1);
        final int o4 = getPointsOrientation(beginX2, beginY2, endX2, endY2,  endX1, endY1);

        // general case
        if(o1 != o2 && o3 != o4) {
            final float a1 = (endY1 - beginY1);
            final float b1 = (beginX1 - endX1);
            final float c1 = (a1 * beginX1 + b1 * beginY1);

            final float a2 = (endY2 - beginY2);
            final float b2 = (beginX2 - endX2);
            final float c2 = (a2 * beginX2 + b2 * beginY2);

            if(isLineIntersectLine(a1, b1, c1,  a2, b2, c2))
                return true;
        }

        // special cases
        return (
            o1 == 0 && isOnSegment(beginX1, beginY1, beginX2, beginY2, endX1, endY1) ||
            o2 == 0 && isOnSegment(beginX1, beginY1, endX2,   endY2,   endX1, endY1) ||
            o3 == 0 && isOnSegment(beginX2, beginY2, beginX1, beginY1, endX2, endY2) ||
            o4 == 0 && isOnSegment(beginX2, beginY2, endX1,   endY1,   endX2, endY2)
        );
    }

    public static boolean isSegmentIntersectSegment(Vec2f begin1, Vec2f end1, Vec2f begin2, Vec2f end2) {
        return isSegmentIntersectSegment(begin1.x, begin1.y, end1.x, end1.y, begin2.x, begin2.y, end2.x, end2.y);
    }

    private static boolean isOnSegment(float ax, float ay, float bx, float by, float cx, float cy) {
        return (
            bx <= Math.max(ax, cx) &&
            bx >= Math.min(ax, cx) &&
            by <= Math.max(ay, cy) &&
            by >= Math.min(ay, cy)
        );
    }


    public static boolean isPointOnSegment(float pointX, float pointY, float ax, float ay, float bx, float by) {
        final float crossproduct = (pointY - ay) * (bx - ax) - (pointX - ax) * (by - ay);

        if(Math.abs(crossproduct) > 0.0001)
            return false;

        final float dotproduct = (pointX - ax) * (bx - ax) + (pointY - ay) * (by - ay);
        if(dotproduct < 0)
            return false;

        final float squaredlengthba = (bx - ax) * (bx - ax) + (by - ay) * (by - ay);
        return !(dotproduct > squaredlengthba);
    }


    public static boolean isPointOnPolygon(float pointX, float pointY, float... vertices) {
        boolean inside = false;

        for(int i = 0; i < vertices.length; i += 2) {
            final float x1 = vertices[i];
            final float y1 = vertices[i + 1];

            final int j = (i + 2) % vertices.length;
            final float x2 = vertices[j];
            final float y2 = vertices[j + 1];

            if(isPointOnSegment(pointX, pointY, x1, y1, x2, y2))
                return true;

            if((pointY < y1) != (pointY < y2) && (pointX < Math.max(x1, x2))) {
                final float intersectX = (pointY - y1) * (x2 - x1) / (y2 - y1) + x1;
                if (x1 == x2 || pointX < intersectX)
                    inside = !inside;
            }
        }
        return inside;
    }

    public static boolean isPointInPolygon(float pointX, float pointY, float... vertices) {
        boolean inside = false;

        for(int i = 0; i < vertices.length; i += 2) {
            final float x1 = vertices[i];
            final float y1 = vertices[i + 1];

            final int j = (i + 2) % vertices.length;
            final float x2 = vertices[j];
            final float y2 = vertices[j + 1];

            if(isPointOnSegment(pointX, pointY, x1, y1, x2, y2))
                return false;

            if(((pointY <= y1) != (pointY <= y2))) {
                final float intersectionX = (pointY - y1) * (x2 - x1) / (y2 - y1) + x1;
                if(pointX <= intersectionX)
                    inside = !inside;
            }
        }
        return inside;
    }


    public static Rect getPolygonBounds(Rect rect, float... vertices) {
        rect.setPosition(Float.MAX_VALUE);
        rect.setSize(0F);

        for(int i = 0; i < vertices.length; i += 2) {
            final float x = vertices[i];
            final float y = vertices[i + 1];
            rect.set(
                Math.min(rect.x, x),
                Math.min(rect.y, y),
                Math.max(rect.width, x),
                Math.max(rect.height, y)
            );
        }

        rect.setSize(rect.width - rect.x, rect.height - rect.y);
        return rect;
    }


    private static class Segment {

        public final Vec2f begin = new Vec2f();
        public final Vec2f end = new Vec2f();
        public boolean markBegin;
        public boolean markEnd;
        public boolean hasIntersection2;
        public final List<Segment> intersectSegments = new ArrayList<>();
        public final Map<Segment, Vec2f> intersectPoints = new HashMap<>();
        public final LinkedList<Vec2f> vertices = new LinkedList<>();

        public Segment(float beginX, float beginY, float endX, float endY) {
            this.begin.set(beginX, beginY);
            this.end.set(endX, endY);
        }

        public void collectVertices() {
            if(!vertices.isEmpty())
                return;
            if(markBegin)
                vertices.add(begin);
            vertices.addAll(intersectPoints.values());
            if(markEnd)
                vertices.add(end);

            vertices.sort(Comparator.comparingDouble(v -> Vec2f.dst(v, begin))); //! dst2
        }

        @Override
        public boolean equals(Object object) {
            if(object == null || getClass() != object.getClass())
                return false;
            final Segment segment = (Segment) object;
            return begin.equals(segment.begin) && end.equals(segment.end);
        }

        @Override
        public int hashCode() {
            return Objects.hash(begin, end);
        }
    }


    public static float[] getPolygonsIntersection(float[] vertices1, float[] vertices2) {
        // get polygons segments
        final LinkedList<Segment> segments1 = new LinkedList<>();
        for(int i = 0; i < vertices1.length; i += 2) {
            final float x1 = vertices1[i];
            final float y1 = vertices1[i + 1];

            final int j = (i + 2) % vertices1.length;
            final float x2 = vertices1[j];
            final float y2 = vertices1[j + 1];

            segments1.add(new Segment(x1, y1, x2, y2));
        }
        final LinkedList<Segment> segments2 = new LinkedList<>();
        for(int i = 0; i < vertices2.length; i += 2) {
            final float x1 = vertices2[i];
            final float y1 = vertices2[i + 1];

            final int j = (i + 2) % vertices2.length;
            final float x2 = vertices2[j];
            final float y2 = vertices2[j + 1];

            segments2.add(new Segment(x1, y1, x2, y2));
        }

        // collect polygon1 intersected segments and vertices
        final LinkedList<Segment> list = new LinkedList<>();
        final Vec2f dst_vec = new Vec2f();

        for(int i = 0; i < segments1.size(); i++) {
            final Segment segmentPrev1 = segments1.get((i + segments1.size() - 1) % segments1.size());
            final Segment segment1 = segments1.get(i);

            if(isPointOnPolygon(segment1.begin.x, segment1.begin.y, vertices2) && !segmentPrev1.end.equals(dst_vec)) {
                list.add(segment1);
                segment1.markBegin = true;
            }

            for(final Segment segment2: segments2) {
                if(getSegmentIntersectSegment(dst_vec, segment1.begin, segment1.end, segment2.begin, segment2.end)) {
                    if(dst_vec.equals(segment1.begin) || dst_vec.equals(segment1.end) || dst_vec.equals(segment2.begin) || dst_vec.equals(segment2.end)){
                        continue;
                    }

                    if(!list.contains(segment1))
                        list.add(segment1);

                    final Vec2f vertex = dst_vec.copy();
                    segment1.intersectSegments.add(segment2);
                    segment1.intersectPoints.put(segment2, vertex);

                    segment2.hasIntersection2 = true;
                }
            }
        }
        if(list.isEmpty())
            return new float[0];

        for(Segment segment: list)
            segment.collectVertices();

        // collect polygon2 vertices
        for(int i = 0; i < list.size(); i++) {
            final Segment segment = list.get(i);

            // if has intersections get intersected segments
            for(int j = 0; j < segment.intersectSegments.size(); j++) {
                final Segment intersectedSegment = segment.intersectSegments.get(j);

                final int segment2Index = segments2.indexOf(intersectedSegment);
                if(segment2Index == -1)
                    continue;
                // iterate segments 2
                int added2Segments = 0;
                int increment = 0;
                for(int k = segment2Index; k < segment2Index + segments2.size() && k > segment2Index - segments2.size(); k += increment){
                    final Segment intersectedSegment2 = segments2.get((k + segments2.size()) % segments2.size());
                    // if has next intersection break iteration
                    if(k != segment2Index && intersectedSegment2.hasIntersection2)
                        break;

                    if(k != 0 && !intersectedSegment2.markBegin && isPointOnPolygon(intersectedSegment2.begin.x, intersectedSegment2.begin.y, vertices1)){
                        intersectedSegment2.markBegin = true;
                        if(increment == 0)
                            increment = -1;
                    }
                    if(k != 0 && !intersectedSegment2.markEnd && isPointOnPolygon(intersectedSegment2.end.x, intersectedSegment2.end.y, vertices1)){
                        intersectedSegment2.markEnd = true;
                        if(increment == 0)
                            increment = 1;
                    }

                    if(increment == 0)
                        break;

                    if((intersectedSegment2.markEnd || intersectedSegment2.markBegin)) {
                        list.add(++i, intersectedSegment2);
                        added2Segments++;
                    }
                }
                //i += added2Segments;
            }
        }

        // collect all vertices
        for(Segment segment: list)
            segment.collectVertices();

        final FloatList vertices = new FloatList();
        int i = 0;
        Segment lastSegment = list.get(0);

        System.out.println();
        while(i < list.size()) {
            final Segment segment = list.get(i); //lastSegment;
            System.out.println("segment_" + i + "_vertices: " + segment.vertices.size() + " (" + segment.intersectPoints.size() + " " + segment.markBegin + " " + segment.markEnd + ")");
            for(Vec2f vertex: segment.vertices)
                vertices.add(vertex.x, vertex.y);

            // search next segment
            //for(Segment listSegment: list){
            //    if(lastSegment.vertices.getLast().equals(listSegment.vertices.getFirst()) || lastSegment.vertices.getFirst().equals(listSegment.vertices.getLast())) {
            //        lastSegment = listSegment;
            //        i++;
            //        break;
            //    }
            //}
            i++;
        }

        return vertices.arrayTrimmed();
    }

}
