package generaloss;

import jpize.util.Rect;
import jpize.util.math.vector.Vec2f;

import java.util.*;

public class Inter {

    public static boolean getLineIntersectLine(Vec2f dst, float a1, float b1, float c1, float a2, float b2, float c2) {
        final float determinant = (a1 * b2 - a2 * b1);
        if(determinant == 0F)
            return false;

        if(dst != null)
            dst.set((b2 * c1 - b1 * c2), (a1 * c2 - a2 * c1)).div(determinant);

        return true;
    }

    public static boolean isLineIntersectLine(float a1, float b1, float c1, float a2, float b2, float c2) {
        final float determinant = (a1 * b2 - a2 * b1);
        return (determinant != 0F);
    }

    public static boolean getSegmentIntersectSegment(Vec2f dst,
                                                     float beginX1, float beginY1, float endX1, float endY1,
                                                     float beginX2, float beginY2, float endX2, float endY2) {
        final float dx1 = (endX1 - beginX1);
        final float dy1 = (endY1 - beginY1);
        final float dx2 = (endX2 - beginX2);
        final float dy2 = (endY2 - beginY2);

        final float denominator = (dx1 * dy2 - dx2 * dy1);
        if(denominator == 0)
            return false; // collinear
        final boolean denomimatorPositive = (denominator > 0);

        final float dx12 = (beginX1 - beginX2);
        final float dy12 = (beginY1 - beginY2);

        final float numerator1 = (dx1 * dy12 - dy1 * dx12);
        if((numerator1 < 0) == denomimatorPositive)
            return false;

        final float numerator2 = (dx2 * dy12 - dy2 * dx12);
        if((numerator2 < 0) == denomimatorPositive)
            return false;

        if(((numerator1 > denominator) == denomimatorPositive) || ((numerator2 > denominator) == denomimatorPositive))
            return false;

        final float t = (numerator2 / denominator);
        if(dst != null) {
            dst.x = (beginX1 + t * dx1);
            dst.y = (beginY1 + t * dy1);
        }
        return true;
    }

    public static boolean getSegmentIntersectSegment(Vec2f dst, Vec2f begin1, Vec2f end1, Vec2f begin2, Vec2f end2) {
        return getSegmentIntersectSegment(dst, begin1.x, begin1.y, end1.x, end1.y, begin2.x, begin2.y, end2.x, end2.y);
    }

    public static boolean isSegmentIntersectSegment(float beginX1, float beginY1, float endX1, float endY1,
                                                    float beginX2, float beginY2, float endX2, float endY2) {

        final float dx1 = (endX1 - beginX1);
        final float dy1 = (endY1 - beginY1);
        final float dx2 = (endX2 - beginX2);
        final float dy2 = (endY2 - beginY2);

        final float denominator = (dx1 * dy2 - dx2 * dy1);
        if(denominator == 0)
            return false; // collinear
        final boolean denomimatorPositive = (denominator > 0);

        final float dx12 = (beginX1 - beginX2);
        final float dy12 = (beginY1 - beginY2);

        final float numerator1 = (dx1 * dy12 - dy1 * dx12);
        if((numerator1 < 0) == denomimatorPositive)
            return false;

        final float numerator2 = (dx2 * dy12 - dy2 * dx12);
        if((numerator2 < 0) == denomimatorPositive)
            return false;

        return ((numerator1 > denominator) != denomimatorPositive) &&
                ((numerator2 > denominator) != denomimatorPositive);
    }

    public static boolean isSegmentIntersectSegment(Vec2f begin1, Vec2f end1, Vec2f begin2, Vec2f end2) {
        return isSegmentIntersectSegment(begin1.x, begin1.y, end1.x, end1.y, begin2.x, begin2.y, end2.x, end2.y);
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


    public static class Segment {

        public final Vec2f begin;
        public final Vec2f end;
        public boolean intersectedBegin;
        public boolean intersectedEnd;
        public int color;

        public Segment(Vec2f begin, Vec2f end) {
            this.begin = begin;
            this.end = end;
        }

        public Segment(float beginX, float beginY, float endX, float endY) {
            this(new Vec2f(beginX, beginY), new Vec2f(endX, endY));
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


    public static /*float[]*/ Queue<Segment> getPolygonsIntersection(float[] vertices1, float[] vertices2) {
        // get polygons segments
        final List<Segment> segments1 = new ArrayList<>();
        final List<Segment> segments2 = new ArrayList<>();
        for(int i = 0; i < vertices1.length; i += 2) {
            final float x1 = vertices1[i];
            final float y1 = vertices1[i + 1];

            final int j = (i + 2) % vertices1.length;
            final float x2 = vertices1[j];
            final float y2 = vertices1[j + 1];

            final Segment segment = new Segment(x1, y1, x2, y2);
            segment.color = 0x0000FF;
            segments1.add(segment);
        }
        for(int i = 0; i < vertices2.length; i += 2) {
            final float x1 = vertices2[i];
            final float y1 = vertices2[i + 1];

            final int j = (i + 2) % vertices2.length;
            final float x2 = vertices2[j];
            final float y2 = vertices2[j + 1];

            final Segment segment = new Segment(x1, y1, x2, y2);
            segment.color = 0x0000FF;
            segments2.add(segment);
        }

        // split segments
        final Vec2f dst_vec = new Vec2f();

        boolean splitted;
        splitLoop : do{
            splitted = false;

            for(int i = 0; i < segments1.size(); i++){
                final Segment segment1 = segments1.get(i);

                for(int j = 0; j < segments2.size(); j++){
                    final Segment segment2 = segments2.get(j);

                    if(getSegmentIntersectSegment(dst_vec, segment1.begin, segment1.end, segment2.begin, segment2.end)){
                        final Vec2f vertex = dst_vec.copy();

                        if(
                            Vec2f.dst2(segment1.begin, vertex) > 0F &&
                            Vec2f.dst2(vertex, segment1.end) > 0F &&
                            Vec2f.dst2(segment2.begin, vertex) > 0F &&
                            Vec2f.dst2(vertex, segment2.end) > 0F
                        ){
                            segments1.remove(segment1);

                            final Segment part11 = new Segment(segment1.begin, vertex);
                            part11.color = 0xFF0000;
                            part11.intersectedBegin = segment1.intersectedBegin;
                            part11.intersectedEnd = true;
                            segments1.add(i, part11);

                            final Segment part12 = new Segment(vertex, segment1.end);
                            part12.color = 0x00FF00;
                            part12.intersectedBegin = true;
                            part12.intersectedEnd = segment1.intersectedEnd;
                            segments1.add(++i, part12);

                            segments2.remove(segment2);

                            final Segment part21 = new Segment(segment2.begin, vertex);
                            part21.color = 0xFF0000;
                            part21.intersectedBegin = segment2.intersectedBegin;
                            part21.intersectedEnd = true;
                            segments2.add(j, part21);

                            final Segment part22 = new Segment(vertex, segment2.end);
                            part22.color = 0x00FF00;
                            part22.intersectedBegin = true;
                            part22.intersectedEnd = segment2.intersectedEnd;
                            segments2.add(++j, part22);

                            splitted = true;
                            continue splitLoop;
                        }
                    }
                }
            }
        }while(splitted);

        // collect segments
        final Queue<Segment> segments = new LinkedList<>();
        for(Segment segment1: segments1){
            if(
                (segment1.intersectedBegin || isPointOnPolygon(segment1.begin.x, segment1.begin.y, vertices2)) &&
                (segment1.intersectedEnd   || isPointOnPolygon(segment1.end.x,   segment1.end.y,   vertices2))
            ){
                segments.add(segment1);
            }else{
                segments.add(segment1);
                segment1.color = 0x777777;
            }
        }
        for(Segment segment2: segments2){
            if(
                (segment2.intersectedBegin || isPointOnPolygon(segment2.begin.x, segment2.begin.y, vertices1)) &&
                (segment2.intersectedEnd   || isPointOnPolygon(segment2.end.x,   segment2.end.y,   vertices1))
            ){
                segments.add(segment2);
            }else{
                segments.add(segment2);
                segment2.color = 0x777777;
            }
        }

        return segments;

        // if(segments.size() < 3)
        //     return new float[0];

        // // collect vertices
        // final FloatList vertices = new FloatList();

        // Vec2f lastVertex = segments.poll().begin;
        // boolean found;
        // do{
        //     vertices.add(lastVertex.x, lastVertex.y);
        //     found = false;

        //     final Iterator<Segment> iterator = segments.iterator();
        //     while(iterator.hasNext()) {
        //         final Segment segment = iterator.next();
        //         if(lastVertex.equals(segment.begin)) {
        //             iterator.remove();
        //             lastVertex = segment.end;
        //             found = true;
        //             break;
        //         }
        //         if(lastVertex.equals(segment.end)) {
        //             iterator.remove();
        //             lastVertex = segment.begin;
        //             found = true;
        //             break;
        //         }
        //     }
        // }while(found);

        // return vertices.arrayTrimmed();
    }

}
