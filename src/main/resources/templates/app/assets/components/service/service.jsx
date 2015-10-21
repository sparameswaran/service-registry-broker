/** @jsx React.DOM */
var React = require('react');
var RegistryServices = require('../shared/registryServices.jsx');

var PageTitle = require('./pageTitle.jsx');
var Plans = require('./plans.jsx');
var _ = require('lodash');


(function () {
  'use strict';


  var Service = React.createClass({


	getInitialState: function() {
	//console.log("Entering into Individual Service page, state contains: " , this.props);
	
    return {
      serviceEntry: {},
      editing: false
    };
  },
  
      _updateState: function(service) {
		this.setState({ serviceEntry : service});
		console.log("Saved state on entering hte service to: ", this.state);
    },
  
    componentDidMount: function() {
    
    
        RegistryServices.findServiceById( this.props.params['serviceId'] ).done(data =>
	    
			this.setState({serviceEntry: data})      	
	    )
	    console.log("Inside componentDidMount in Services page, this.props contains: " , this.props, " and state contains: " , this.state);
    },


    render: function() {
    
    
    //var serviceEntry = _.find(this.props.services, { id : this.props.params['serviceId'] } );
    
    console.log("Inside render for Individual Service page, passing this.props: ", this.props , " while state has", this.state);
    var serviceEntry = this.state.serviceEntry;
      return (
        <div>
          <PageTitle serviceEntry={serviceEntry} />
          <Plans serviceEntry={serviceEntry}/>
        </div>
      );
    }
  });

  module.exports = Service;

}());
