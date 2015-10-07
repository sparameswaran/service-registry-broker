/** @jsx React.DOM */
var React = require('react');

// PUI
var Divider = require('pui-react-dividers').Divider;
var BaseCollapse = require('pui-react-collapse').BaseCollapse;
var Label = require('pui-react-labels').Label;
var InlineList = require('pui-react-lists').InlineList;
var ListItem = require('pui-react-lists').ListItem;

(function () {
  'use strict';

  var Plans = React.createClass({

    render: function() {
      return (
        <div className="plan paxl">
          <p className="mvn type-dark-4 type-xs em-alt em-default label-alt">Free Plan</p>
          <p className="type-dark-1 mvn em-high mts">
            {this.props.planTitle}
          </p>
          <p className="type-dark-4 mvn">{this.props.planDescription}</p>
          <p className="type-dark-4 mvn type-sm mtl">
            <span className="mrl em-alt">
              Categories:
              <InlineList spacing="l">
                <ListItem>Big Data</ListItem>
                <ListItem>Messaging Queue</ListItem>
                <ListItem>Productivity Tool</ListItem>
              </InlineList>
            </span>
          </p>
          <Divider className="mvl" />
          <BaseCollapse header="Show Associated Credentials" className="service-plan-collapse mvn">
            <pre className="mvn"><code className="language-ruby">class Foo
              def bar
                puts 'hi'
              end
            end</code></pre>
          </BaseCollapse>
        </div>
      );
    }
  });

  module.exports = Plans;

}());
