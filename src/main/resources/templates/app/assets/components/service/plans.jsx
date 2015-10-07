/** @jsx React.DOM */
var React = require('react');

// PUI
var BasicPanelAlt = require('pui-react-panels').BasicPanelAlt;
var DefaultAltButton = require('pui-react-buttons').DefaultAltButton;
var Divider = require('pui-react-dividers').Divider;
var BaseCollapse = require('pui-react-collapse').BaseCollapse;

var Plan = require('./plan.jsx');

(function () {
  'use strict';

  var PlanTitle =
    <div className="media">
      <div className="media-middle media-body">
        <p className="type-dark-1 h4 mvn em-high">Big Data Base Service Plans</p>
      </div>
      <div className="media-middle media-body txt-r">
        <DefaultAltButton>Add New Plan</DefaultAltButton>
      </div>
    </div>
  ;

  var Plans = React.createClass({

    render: function() {
      return (
        <div className="container">
          <BasicPanelAlt title={PlanTitle} className="mvxl" id="plan-panel">
            <Plan planTitle="Basic" planDescription="Basic Plan throttled to 5 connections per second." />
            <Divider className="mvn" />
            <Plan planTitle="Premium" planDescription="Premium Plan throttled to 25 connections per second." />
          </BasicPanelAlt>
        </div>
      );
    }
  });

  module.exports = Plans;

}());
