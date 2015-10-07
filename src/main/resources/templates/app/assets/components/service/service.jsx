/** @jsx React.DOM */
var React = require('react');

var PageTitle = require('./pageTitle.jsx');
var Plans = require('./plans.jsx');

(function () {
  'use strict';

  var Service = React.createClass({

    render: function() {
      return (
        <div>
          <PageTitle title="Big Data Base" />
          <Plans />
        </div>
      );
    }
  });

  module.exports = Service;

}());
