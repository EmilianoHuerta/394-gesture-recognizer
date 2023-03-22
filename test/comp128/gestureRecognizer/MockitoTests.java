package comp128.gestureRecognizer;

import edu.macalester.graphics.*;
import edu.macalester.graphics.ui.Button;
import edu.macalester.graphics.ui.TextField;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

public class MockitoTests {

    GestureApp gestureApp = mock(GestureApp.class);
    CanvasWindow canvas = mock(CanvasWindow.class);


    @Test
    void mockitoExperiment() {
        gestureApp.matchLabel = new GraphicsText("Match: ");
        gestureApp.matchLabel.setFont(FontStyle.PLAIN, 24);
        canvas.add(gestureApp.matchLabel, 10, 30);

        gestureApp.uiGroup = new GraphicsGroup();

        gestureApp.templateNameField = new TextField();

        gestureApp.addTemplateButton = new Button("Add Template");
        gestureApp.addTemplateButton.onClick( () -> gestureApp.addTemplate() );

        Point center = new Point(300.0, 300.0);
        double fieldWidthWithMargin = gestureApp.templateNameField.getSize().getX() + 5;
        double totalWidth = fieldWidthWithMargin + gestureApp.addTemplateButton.getSize().getX();


        gestureApp.uiGroup.add(gestureApp.templateNameField, center.getX() - totalWidth/2.0, 0);
        gestureApp.uiGroup.add(gestureApp.addTemplateButton, gestureApp.templateNameField.getPosition().getX() + fieldWidthWithMargin, 0);
        canvas.add(gestureApp.uiGroup, 0, 600.0 - gestureApp.uiGroup.getHeight());

        Consumer<Character> handleKeyCommand = ch -> gestureApp.keyTyped(ch);
        canvas.onCharacterTyped(handleKeyCommand);
    }
}
