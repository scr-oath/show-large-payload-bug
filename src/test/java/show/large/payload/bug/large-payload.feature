# Created by scr at 3/18/21
Feature: Simple post requests of different sizes
  In order to display the bug in sending large payloads, send a few payloads.

  Scenario Outline: Make a request to local server using #inputFile
    Given url karate.properties['uri']
    And form field text = karate.readAsString(inputFile)
    When method post
    Then status 200
    And match $.message == 'OK'

    Examples:
      | inputFile       | url                                                                   |
      | data/small.html | http://help.websiteos.com/websiteos/example_of_a_simple_html_page.htm |
      | data/big.html   | http://demo.borland.com/Testsite/stadyn_largepagewithimages.html      |
