package comp128.gestureRecognizer;

import edu.macalester.graphics.*;
import edu.macalester.graphics.Point;
import edu.macalester.graphics.events.MouseButtonEvent;
import edu.macalester.graphics.events.MouseMotionEvent;
import edu.macalester.graphics.ui.Button;
import edu.macalester.graphics.ui.TextField;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;

/**
 * The window and user interface for drawing gestures and automatically recognizing them
 * Created by bjackson on 10/29/2016.
 */
public class GestureApp {

    private CanvasWindow canvas;
    private Recognizer recognizer;
    private IOManager ioManager;
    private GraphicsGroup uiGroup;
    private Button addTemplateButton;
    private TextField templateNameField;
    private GraphicsText matchLabel;
    private Deque<Point> path;

    public GestureApp(){
        canvas = new CanvasWindow("Gesture Recognizer", 600, 600);
        recognizer = new Recognizer();
        path = new ArrayDeque<>();
        ioManager = new IOManager();
        setupUI();
    }

    /**
     * Create the user interface
     */
    private void setupUI(){
        matchLabel = new GraphicsText("Match: ");
        matchLabel.setFont(FontStyle.PLAIN, 24);
        canvas.add(matchLabel, 10, 30);

        uiGroup = new GraphicsGroup();

        templateNameField = new TextField();

        addTemplateButton = new Button("Add Template");
        addTemplateButton.onClick( () -> addTemplate() );

        Point center = canvas.getCenter();
        double fieldWidthWithMargin = templateNameField.getSize().getX() + 5;
        double totalWidth = fieldWidthWithMargin + addTemplateButton.getSize().getX();


        uiGroup.add(templateNameField, center.getX() - totalWidth/2.0, 0);
        uiGroup.add(addTemplateButton, templateNameField.getPosition().getX() + fieldWidthWithMargin, 0);
        canvas.add(uiGroup, 0, canvas.getHeight() - uiGroup.getHeight());

        Consumer<Character> handleKeyCommand = ch -> keyTyped(ch);
        canvas.onCharacterTyped(handleKeyCommand);

        canvas.onMouseDown(this::mousePressed);
        canvas.onDrag(this::mouseDragged);
        canvas.onMouseUp(this::mouseReleased);

    }

    /**
     * This method monitors the press function of the mouse, draws a line with what the user wishes to draw 
     * @param event
     */
    public void mousePressed(MouseButtonEvent event){
        removeAllNonUIGraphicsObjects();
        Point position = event.getPosition();
        path.add(position); 
        double lineX = position.getX(); 
        double lineY = position.getY();


        Line newLine = new Line(lineX, lineY, position.getX(), position.getY());
        canvas.add(newLine); 
    }
    /**
     * This method monitors the drag function of the mouse 
     * @param event
     */
    public void mouseDragged(MouseMotionEvent event){

        Line newLine = new Line(path.getLast().getX(), path.getLast().getY(), event.getPosition().getX(), event.getPosition().getY()); 
        
        Point position = event.getPosition();
        path.add(position);

        canvas.add(newLine);

    }
    /**
     * This method monitors the on released function of the mouse, matches template and sets name and score 
     * @param event
     */
    public void mouseReleased(MouseButtonEvent event){
        HashMap<String, Double> stringName = recognizer.bestTemplate(path);
         String key = "";
         double dequeValue = 0; 
        for(Entry<String, Double> templateElement: stringName.entrySet()){
            key = templateElement.getKey();
            dequeValue = templateElement.getValue(); 
        }
        matchLabel.setText("Match: " + key + " " + dequeValue);
    }

    /**
     * Clears the canvas, but preserves all the UI objects
     */
    private void removeAllNonUIGraphicsObjects() {
        canvas.removeAll();
        canvas.add(matchLabel);
        canvas.add(uiGroup);
    }

    /**
     * Handle what happens when the add template button is pressed. This method adds the points stored in path as a template
     * with the name from the templateNameField textbox. If no text has been entered then the template is named with "no name gesture"
     */
    private void addTemplate() {
        String name = templateNameField.getText(); 
        if (name.isEmpty()){
            name = "no name gesture";
        }
        if(path.size() > 0){
            recognizer.addTemplate(name, path); // Add the points stored in the path as a template
        }  
    }

    /**
     * Handles keyboard commands used to save and load gestures for debugging and to write tests.
     * Note, once you type in the templateNameField, you need to call canvas.requestFocus() in order to get
     * keyboard events. This is best done in the mouseDown callback on the canvas.
     */
    public void keyTyped(Character ch) {
        if (ch.equals('L')){
            String name = templateNameField.getText();
            if (name.isEmpty()){
                name = "gesture";
            }
            Deque<Point> points = ioManager.loadGesture(name+".xml");
            if (points != null){
                recognizer.addTemplate(name, points);
                System.out.println("Loaded "+name);
            }
        }
        else if (ch.equals('s')){
            String name = templateNameField.getText();
            if (name.isEmpty()){
                name = "gesture";
            }
            ioManager.saveGesture(path, name, name+".xml");
            System.out.println("Saved "+name);
        }
    }

    public static void main(String[] args){
        GestureApp window = new GestureApp();
    }
}
