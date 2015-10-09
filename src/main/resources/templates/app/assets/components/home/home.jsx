/** @jsx React.DOM */
var React = require('react');

// All the PUI
var Row = require('pui-react-grids').Row;
var Col = require('pui-react-grids').Col;

var Search = require('./search.jsx');
var Services = require('./services.jsx');
var PageTitle = require('./pageTitle.jsx');

(function () {
  'use strict';

  var Home = React.createClass({

    render: function() {
      console.log("This home contains: " , this.props.services);
      return (
        <div>
          <PageTitle />
          <Services services={this.props.services} />
          
          
        </div>
      );
    }
  });

  
  module.exports = Home;

}());
