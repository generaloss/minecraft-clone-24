package generaloss;

import jpize.util.math.geometry.Intersector;
import jpize.util.math.geometry.Polygon;
import jpize.util.math.vector.Vec2f;

import java.util.*;

public class MethodDev {

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

                    if(Intersector.getSegmentIntersectSegment(dst_vec, segment1.begin, segment1.end, segment2.begin, segment2.end)){
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
                (segment1.intersectedBegin || Polygon.isPointOn(segment1.begin.x, segment1.begin.y, vertices2)) &&
                (segment1.intersectedEnd   || Polygon.isPointOn(segment1.end.x,   segment1.end.y,   vertices2))
            ){
                segments.add(segment1);
            }else{
                segments.add(segment1);
                segment1.color = 0x777777;
            }
        }
        for(Segment segment2: segments2){
            if(
                (segment2.intersectedBegin || Polygon.isPointOn(segment2.begin.x, segment2.begin.y, vertices1)) &&
                (segment2.intersectedEnd   || Polygon.isPointOn(segment2.end.x,   segment2.end.y,   vertices1))
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
