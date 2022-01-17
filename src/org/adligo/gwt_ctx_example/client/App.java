package org.adligo.gwt_ctx_example.client;

import java.util.function.Supplier;

import org.adligo.ctx.shared.Ctx;
import org.adligo.ctx.shared.CtxMutant;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class App implements EntryPoint {
  /**
   * The message displayed to the user when the server cannot be reached or
   * returns an error.
   */
  private static final String SERVER_ERROR = "An error occurred while "
      + "attempting to contact the server. Please check your network "
      + "connection and try again.";
  private final Ctx ctx;

  native void println( String message) /*-{
  console.log( "me:" + message );
}-*/;
  
  public App() {
    println("In App()");
    CtxMutant cm = new CtxMutant();
    cm.add("name", "App");

    cm.addCreator(String.class.getName(),
      //lambda's work fine in GWT now cool, first time I have checked,
      // however the complier will cast them to Object, on occasion, which
      // gets weird
      () -> { return "Supplier Check!"; });
        

    ctx = new Ctx(cm);
    System.out.println("Exit App()");
  }
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
    ctx.print("in onModuleLoad");
    final Button sendButton = new Button("Send");
    final TextBox nameField = new TextBox();
    nameField.setText("GWT User");
    final Label errorLabel = new Label();

    // We can add style names to widgets
    sendButton.addStyleName("sendButton");

    // Add the nameField and sendButton to the RootPanel
    // Use RootPanel.get() to get the entire body element
    RootPanel.get("nameFieldContainer").add(nameField);
    RootPanel.get("sendButtonContainer").add(sendButton);
    RootPanel.get("errorLabelContainer").add(errorLabel);

    // Focus the cursor on the name field when the app loads
    nameField.setFocus(true);
    nameField.selectAll();

    // Create the popup dialog box
    ctx.print("setting up dialogBox");
    final DialogBox dialogBox = new DialogBox();
    dialogBox.setText("Dialog Box");
    dialogBox.setAnimationEnabled(true);
    final Button closeButton = new Button("Close");
    // We can set the id of a widget by accessing its Element
    closeButton.getElement().setId("closeButton");
    final Label textToServerLabel = new Label();
    final HTML serverResponseLabel = new HTML();
    VerticalPanel dialogVPanel = new VerticalPanel();
    dialogVPanel.addStyleName("dialogVPanel");
    dialogVPanel.add(new HTML("<b>Dialog Box</b>"));
    dialogVPanel.add(textToServerLabel);
    dialogVPanel.add(new HTML("<br><b>Server replies:</b>"));
    dialogVPanel.add(serverResponseLabel);
    dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_RIGHT);
    dialogVPanel.add(closeButton);
    dialogBox.setWidget(dialogVPanel);

    // Add a handler to close the DialogBox
    closeButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        dialogBox.hide();
        sendButton.setEnabled(true);
        sendButton.setFocus(true);
      }
    });

    // Create a handler for the sendButton and nameField
    class MyHandler implements ClickHandler, KeyUpHandler {
      /**
       * Fired when the user clicks on the sendButton.
       */
      public void onClick(ClickEvent event) {
        showDialog();
      }

      /**
       * Fired when the user types in the nameField.
       */
      public void onKeyUp(KeyUpEvent event) {
        if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
          showDialog();
        }
      }

      /**
       * Send the name from the nameField to the server and wait for a response.
       */
      private void showDialog() {
         println("in showDialog with ctx " + ctx);
         println("ctx println");
         println("ctx get name " + ctx.get("name"));
         println("ctx get String.class " + ctx.create(String.class.getName()));
         dialogBox.setText("got name " + ctx.get("name") + " and supplied value " +
              ctx.get(String.class.getName()) + " from Adligo's ctx.");
          serverResponseLabel.addStyleName("serverResponseLabelError");
          dialogBox.center();
          closeButton.setFocus(true);
      }
    }

    // Add a handler to send the name to the server
    MyHandler handler = new MyHandler();
    sendButton.addClickHandler(handler);
    nameField.addKeyUpHandler(handler);
  }
}
