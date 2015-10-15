/** @jsx React.DOM */
var React = require('react');

// PUI
var Divider = require('pui-react-dividers').Divider;
var BaseCollapse = require('pui-react-collapse').BaseCollapse;
var Label = require('pui-react-labels').Label;
var InlineList = require('pui-react-lists').InlineList;
var ListItem = require('pui-react-lists').ListItem;
var Credentials = require('./credentials.jsx');
var RegistryServices = require('../shared/registryServices.jsx');

(function () {
  'use strict';

  var Plan = React.createClass({

	  getInitialState: function() {
	    return {
	      credAttribs: []
	    };
	  },
  
    componentDidMount: function() {
    
        console.log("Checking for credentials of plan: ", this.props.id);
        RegistryServices.getCredentialsForPlans(this.props.id).done(credAttribs =>
	    
			this.setState({credentials: credAttribs})      	
	    );
	    console.log("Got credentials now...: ", this.state.credentials);
    },
    
    render: function() {
      // Cost comes as a map of one entry: { "usd" : 0 }
      // Remove the quotes, and reorder the amount and currency
      var costPrice = JSON.stringify(this.props.metadata.cost.amount).replace(/\"/g, '');
      costPrice = costPrice.substr(1, costPrice.length - 2).split(':').reverse().join(' ');
      
      return (
        <div className="plan paxl">
          <p className="type-dark-1 mvn em-high mts">
            {this.props.name}
          </p>
          <p className="type-dark-4 mvn">{ this.props.description }</p>
          <p className="type-dark-4 mvn type-sm mtl">
	        <p className="mvn type-dark-4 type-xs em-alt em-default label-alt">
	          Free Plan: {this.props.free? "true" : "false"}
	        </p>
	        <p className="type-dark-4  mvn type-sm mtl">            
              Cost : { costPrice }, charged {this.props.metadata.cost.unit }
          	</p>
          </p>
          
          <p className="type-dark-4 mvn type-sm mtl">
            <span className="mrl em-alt">
              Categories:
              <InlineList spacing="l">
                <ListItem>{ this.props.metadata.bullets }</ListItem>
              </InlineList>
            </span>
          </p>
          
          <Divider className="mvl" />
          <BaseCollapse header="Show Associated Credentials" className="service-plan-collapse mvn">
            <Credentials creds={this.state.credentials} />
          </BaseCollapse>
          <Divider/>
        </div>
      );
    }
  });

  module.exports = Plan;

}());
