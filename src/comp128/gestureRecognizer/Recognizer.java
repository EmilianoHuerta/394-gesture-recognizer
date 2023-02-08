package comp128.gestureRecognizer;

import edu.macalester.graphics.CanvasWindow;
import edu.macalester.graphics.Ellipse;
import edu.macalester.graphics.GraphicsGroup;
import edu.macalester.graphics.Point;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Recognizer to recognize 2D gestures. Uses the $1 gesture recognition algorithm.
 */
public class Recognizer {
    
    private HashMap<String, Deque<Point>> templates = new HashMap<String, Deque<Point>>(); 
    private final double SIZE = 250;
    private final int N = 64; 

    /**
     * Constructs a recognizer object
     */
    public Recognizer(){

    }


    /**
     * Create a template to use for matching
     * @param name of the template
     * @param points in the template gesture's path
     */
    public void addTemplate(String name, Deque<Point> points){
        templates.put(name, sampledTemplate(points));
    }
    /**
     * this method returns a sampled series of points, which is implemented by resample, indicativeAngle, rotateBy, and scaleTo
     * @param points
     * @return Deque of points after being resampled, rotated, scaled, and translated
     */
    public Deque<Point> sampledTemplate(Deque<Point> points){
        Deque<Point> sampled = resample(points, N);
        double indicativeAngle = indicativeAngle(sampled);
        sampled = rotateBy(sampled, -indicativeAngle);
        sampled = scaleTo(sampled, SIZE);
        return sampled = translateTo(sampled, new Point(0,0));
    }
    /**
     * This method resamples the amount of points within the gesture to 64 points
     * @param points
     * @param n
     * @return
     */
    public  Deque<Point> resample(Deque<Point> points, int n){
        double totalLength = 0;
        double accumulatedDistance = 0;
        double segmentDistance = 0; 
        ArrayList<Point> pointList = new ArrayList<Point>(); 
        Deque<Point> resampled = new ArrayDeque<Point>();

        Iterator<Point> itr = points.iterator(); 
        while(itr.hasNext()){
            pointList.add(itr.next()); 
        }
        for(int i = 1; i < pointList.size(); i++){
            totalLength += pointList.get(i).distance(pointList.get(i-1)); 
        }
        double resampleInterval = totalLength/(n-1); 
        resampled.add(pointList.get(0)); 
        Iterator<Point> iterator = pointList.iterator(); 

        Point previousPoint = iterator.next();
        Point current = iterator.next(); 

   
        while(true){
            segmentDistance = current.distance(previousPoint); 
            if(segmentDistance  + accumulatedDistance >= resampleInterval){
                resampled.add(Point.interpolate(previousPoint, current, ((resampleInterval - accumulatedDistance) / segmentDistance))); 
                previousPoint = resampled.getLast(); 
                accumulatedDistance = 0; 
            } else {
                if(iterator.hasNext() == false){
                    break; 
                }
                accumulatedDistance += segmentDistance; 
                previousPoint = current; 
                current = iterator.next(); 
            }
        }

        if (segmentDistance < resampleInterval && resampled.size() < n){
            resampled.add(pointList.get(pointList.size() - 1)); 
        }           

        return resampled;
        
    }

    /**
     * This method rotates the gesture by the indicative angle at the center point of the gesture 
     * @param points
     * @param indicativeAngle
     * @return Deque of points after being rotated 
     */
    public Deque<Point> rotateBy(Deque<Point> points, double indicativeAngle){
        Deque<Point> rotateDeque = new ArrayDeque<>(); 

        Point middlePoint = centerPoint(points); 


        for(Point point: points){
            rotateDeque.offerLast(point.rotate(indicativeAngle, middlePoint)); 
        }
        return rotateDeque;
    }

    /**
     * Calculates the middle point within the gesture 
     * @param points
     * @return Point center Point 
     */
    public Point centerPoint(Deque<Point> points){
        double centerX =0; 
        double centerY =0; 

        for(Point point: points){
            centerX += point.getX(); 
            centerY += point.getY(); 
        }
        centerX = centerX / points.size(); 
        centerY = centerY / points.size();   

        Point middlePoint = new Point(centerX, centerY); 

        return middlePoint; 
    }

   
    /**
     * Calculate the indicative angle by finding the centroid of the points by finding the average of the x and y 
     * @param points
     * @return double indicative angle 
     */
    public double indicativeAngle(Deque<Point> points){
        Point middlePoint = centerPoint(points); 
        double xAverage = middlePoint.getX() - points.getFirst().getX(); 
        double yAverage = middlePoint.getY() - points.getFirst().getY(); 

        double indicativeAngle =  Math.atan2(yAverage, xAverage);

        return indicativeAngle;  
    }

    /**
     * Calculate bounding size (width and height) of the gesture and returns it as a point 
     * @param points
     * @return Point location of the bounding box 
     */
    public Point boundingBox(Deque<Point> points){
        double maxX = points.peekFirst().getX(); 
        double maxY = points.peekFirst().getY(); 
        double minX = points.peekFirst().getX(); 
        double minY = points.peekFirst().getY(); 

        for( Point point: points){
            if(point.getX() > maxX){
                maxX = point.getX(); 
            }
            if(point.getX() < minX){
                minX = point.getX(); 
            }
            if(point.getY() < minY){
                minY = point.getY(); 
            }
            if(point.getY() > maxY){
                maxY = point.getY(); 
            }  
        }
        double height = maxY - minY; 
        double width = maxX - minX; 
        Point newPoint = new Point(width, height);

        return newPoint; 
    }

    /**
     * This method scales each point in the gesture by size/width, size/height using the bounding box method
     * @param points
     * @param size
     * @return deque of scaled points 
     */
    public Deque<Point> scaleTo(Deque<Point> points, double size){
        Deque<Point> newDeque = new ArrayDeque<Point>(); 
        Point boundingPoint = boundingBox(points); 
        
        for(Point point: points){
            // I think the error comes in here 
            newDeque.add(point.scale(size/boundingPoint.getX(), size/boundingPoint.getY())); 
        }
        return newDeque;
    }

 
    /**
     * Calculates the centroid of the points with centerPoint by adding k and subtracting the centerPoint -- returns a new deque of points
     * @param points
     * @param k
     * @return deque of centered points 
     */
    public Deque<Point> translateTo(Deque<Point> points, Point k){
        Deque<Point> newPoints = new ArrayDeque<Point>(); 
        Point centerPoint = centerPoint(points);
        for(Point point: points){
            newPoints.add(point.add(k).subtract(centerPoint));
        }
        return newPoints;

    }

    /**
     * Uses a golden section search to calculate rotation that minimizes the distance between the gesture and the template points.
     * @param points
     * @param templatePoints
     * @return best distance
     */
    private double distanceAtBestAngle(Deque<Point> points, Deque<Point> templatePoints){
        double thetaA = -Math.toRadians(45);
        double thetaB = Math.toRadians(45);
        final double deltaTheta = Math.toRadians(2);
        double phi = 0.5*(-1.0 + Math.sqrt(5.0));// golden ratio
        double x1 = phi*thetaA + (1-phi)*thetaB;
        double f1 = distanceAtAngle(points, templatePoints, x1);
        double x2 = (1 - phi)*thetaA + phi*thetaB;
        double f2 = distanceAtAngle(points, templatePoints, x2);
        while(Math.abs(thetaB-thetaA) > deltaTheta){
            if (f1 < f2){
                thetaB = x2;
                x2 = x1;
                f2 = f1;
                x1 = phi*thetaA + (1-phi)*thetaB;
                f1 = distanceAtAngle(points, templatePoints, x1);
            }
            else{
                thetaA = x1;
                x1 = x2;
                f1 = f2;
                x2 = (1-phi)*thetaA + phi*thetaB;
                f2 = distanceAtAngle(points, templatePoints, x2);
            }
        }
        return Math.min(f1, f2);
    }
    /**
     * 
     * @param points
     * @return double total length
     * This methods takes the distance between each point and finds the sum
     */
    public double pathLength(Deque<Point> points){
        double totalLength = 0; 
        Point savePoint = points.getFirst(); 

        for(Point point: points){
            if( point != points.getFirst()){
                totalLength = totalLength + point.distance(savePoint); 
            }
            savePoint = point; 
        }
        return totalLength;
    }

    /**
     * This method rotates a gesture on each point and then determines the new path distance at that rotated angle 
     * @param points
     * @param templatePoints
     * @param theta
     * @return double path Distance 
     */
    public double distanceAtAngle(Deque<Point> points, Deque<Point> templatePoints, double theta){
        Deque<Point> rotatedPoints = null;
        rotatedPoints = rotateBy(points, theta);
        return pathDistance(rotatedPoints, templatePoints);
    }

    /**
     * This method takes two deques, a the original gesture and b the newly rotated deque, iterates through the deque in order to find the distance between the two deques
     * after detemining the length divides by the size of the deque 
     * @param a
     * @param b
     * @return
     */
    double pathDistance(Deque<Point> a, Deque<Point> b){
        Iterator<Point> iteratorA = a.iterator(); 
        Iterator<Point> iteratorB = b.iterator();
        double accumulator = 0;     
        
        while(iteratorA.hasNext() && iteratorB.hasNext()){
            Point pointA = iteratorA.next();
            Point pointB = iteratorB.next(); 
            double distance = pointA.distance(pointB); 
            accumulator += distance; 
        }

        return accumulator / a.size();
    }

    /**
     * This method determines the bestTemplate in the set in order to compared the gesture.
     * @param points
     * @return HashMap with string key (name of template) and double value (score of recognition)
     */
    public HashMap<String, Double> bestTemplate(Deque<Point> points){

        points = sampledTemplate(points);
        double score = Double.MAX_VALUE; 
        double currentScore = 0;
        String bestTemplate = ""; 
        for(Map.Entry<String, Deque<Point>> templateElement: templates.entrySet()){
            String key = templateElement.getKey();
            Deque<Point> dequeValue = templateElement.getValue(); 
            currentScore = distanceAtBestAngle(points, dequeValue);

            if(currentScore < score){
                score = currentScore;
                bestTemplate = key; 
            }
        }
        HashMap<String, Double> finalScore = new HashMap<String, Double>(); 
        score = 1 - score / (Math.sqrt(0.5) * SIZE);
        finalScore.put(bestTemplate, score); 
        return finalScore;
    }

    public HashMap<String, Deque<Point>> getTemplateList() {
        return templates;
    }

}