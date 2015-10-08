/** @jsx React.DOM */
var React = require('react');

// All the PUI
var Icon = require('pui-react-iconography').Icon;
var Row = require('pui-react-grids').Row;
var Col = require('pui-react-grids').Col;
var Divider = require('pui-react-dividers').Divider;
var TileLayout = require('pui-react-tile-layout');
var ClickableAltPanel = require('pui-react-panels').ClickableAltPanel;
var MarketingH1 = require('pui-react-typography').MarketingH1;
var SearchInput = require('pui-react-search-input').SearchInput;
var _ = require('lodash');
var Service = require('./service.jsx');
var RegistryServices = require('../shared/registryServices.jsx');

(function () {
  'use strict';

  var Services = React.createClass({

	getInitialState: function() {
    return {
      serviceObjects: []
    };
  },
  
    componentDidMount: function() {
    
	    RegistryServices.findAllServices().done(function(serviceObjects) {
			this.setState({serviceObjects: serviceObjects})      	
	    });
	
    }, 
    
    
    render: function() { 
    console.log(this.state)
	var services = _.map(this.state.serviceObjects, function(data) { return React.createElement(Service, data); } );
       
      return (
        <div className="pvxl">
          <div className="container">
            <TileLayout columns={{xs: 1, sm: 2, md: 3}}>

                {services}          

            </TileLayout>
          </div>
        </div>
      );
    }
  });

  module.exports = Services;

}());
