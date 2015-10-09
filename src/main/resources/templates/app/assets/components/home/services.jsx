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
	console.log("Entering into Services page, state contains: " , this.props);
    return {

      services :  []
    };
  },
  
   
    render: function() { 
    console.log(this.state)
    console.log("Inside render in Services page, props contains: " , this.props, " and state contains: " , this.state, " and service Entry is : " , this.serviceEntry);
	var services = _.map(this.props.services, function(data) { console.log("Inside map against create Service Element, data : ", data);
	 return React.createElement(Service, data); } );
       
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
