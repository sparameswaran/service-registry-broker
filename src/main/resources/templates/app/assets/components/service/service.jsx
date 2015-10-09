/** @jsx React.DOM */
var React = require('react');

var PageTitle = require('./pageTitle.jsx');
var Plans = require('./plans.jsx');
var _ = require('lodash');

(function () {
  'use strict';


  var Service = React.createClass({


	getInitialState: function() {
	//console.log("Entering into Individual Service page, state contains: " , this.props);
	
    return {
      serviceEntry: {}
    };
  },
  
    componentDidMount: function() {
      console.log("Inside componentDidMount in Services page, this params : ", this.props.params);
      console.log("Inside componentDidMount in Services page, this.props contains: " , this.props, " and state contains: " , this.state);
    }, 
    


    render: function() {
    
    
    var serviceEntry = _.find(this.props.services, { id : this.props.params['serviceId'] } );
    //console.log("Inside render for Services page, passing this.props: ", this.props , " and serviceEntry : ", serviceEntry);
    
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
