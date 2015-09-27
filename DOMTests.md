## quickstartAdd.js - JS file under test ##
```
var JSDEMO = JSDEMO || {};

/**
 * A quickstart object
 * @namespace
 */
JSDEMO.quickstart = function () {

    /**
     * Public interface
     * @scope JSDEMO.quickstart
     */
    return {

        /**
         * Adds two numbers ...
         * @param number1 first number
         * @param number2 second number
         * @return the sum of the two numbers
         */
        addNumbers : function(number1, number2) {
            return number1 + number2;
        },
        addNumbers2 : function(number1, number2) {
            return number1 + number2;
        },
        appendChildNodeToDom : function(addToElement) {
            var newnode = document.createTextNode('Here is some content.');
            document.getElementById(addToElement).appendChild(newnode);
        }
    };
} ();
```

## quickstartAddTest.js - JS Test File corresponding to the above JS File ##
```
test("Can append child to element in DOM", function() {

    // Setup - This is the content of the div we expect to add to
    var expectedTestDivHtml = "Here is some content.";
 
    // Setup - Call the provided DOM setup function
    DOMSetup('<div id="testDiv"/>');
 
    // Execute - Call the function under test that will update the DOM
    SDEMO.quickstart.appendChildNodeToDom('testDiv');
 
    // Assert - Assert that the function performed what we thought 
    equal(expectedTestDivHtml, $('#testDiv').html());
 
    // Teardown - Call the provided DOM teardown function
    DOMTearDown('#testDiv');
});
```