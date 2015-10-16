/** @jsx React.DOM */
var React = require('react');
var RegistryServices = require('../shared/registryServices.jsx');
var SearchInput = require('pui-react-search-input').SearchInput;
var DefaultButton = require('pui-react-buttons').DefaultButton;

// All the PUI
var Row = require('pui-react-grids').Row;
var Col = require('pui-react-grids').Col;

var Search = require('./search.jsx');
var Services = require('./services.jsx');
var PageTitle = require('./pageTitle.jsx');

(function () {
  'use strict';

  var updateComponent = false;
  
  var Home = React.createClass({

  getInitialState: function() {
    return {
      services: []
    };
  },
  
    componentWillMount: function() {
      console.log("Entered componentWillMount");
      
      RegistryServices.findAllServices().done(this._updateState);
	   updateComponent = true;
    },
    
    _updateState: function(services) {
		this.setState({ services });
    },

    
    render: function() {
        	
      return (
        <div>
	        <div className="page-title bg-neutral-11 pvxl">
	          <div className="container">
	            <div className="media">
	              <div className="media-body media-middle">
	                <p className='h1 type-dark-1 mvn em-low'>Services</p>
	              </div>
	              <div className="media-body media-right">                            
	                <p> <Search services={this.state.services} /> </p>
	                <a class="btn" href="/#/addServices">Add Services</a>
	              </div>
	            </div>
	          </div>      
	        </div>
	        
          	<Services services={this.state.services} />
          
        </div>
      );
    }
  });

  
  module.exports = Home;

}());
