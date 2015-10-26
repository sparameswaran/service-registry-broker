/** @jsx React.DOM */
var React = require('react');

// PUI
var Divider = require('pui-react-dividers').Divider;
var BaseCollapse = require('pui-react-collapse').BaseCollapse;
var Label = require('pui-react-labels').Label;
var InlineList = require('pui-react-lists').InlineList;
var GroupList = require('pui-react-lists').GroupList;
var ListItem = require('pui-react-lists').ListItem;
var Credentials = require('./credentials.jsx');
var RegistryServices = require('../shared/registryServices.jsx');


var DefaultAltButton = require('pui-react-buttons').DefaultAltButton;
var DefaultButton = require('pui-react-buttons').DefaultButton;
var Modal = require('pui-react-modals').Modal;
var ModalBody = require('pui-react-modals').ModalBody;
var ModalFooter = require('pui-react-modals').ModalFooter;
var Image = require('pui-react-images');
var Router = require('react-router');

(function () {
  'use strict';

  var Plan = React.createClass({

	  getInitialState: function() {
	    return {
	      credAttribs: []
	    };
	  },
  
    _openDeleteModal: function(){
	    console.log("Got open modal event!...");
	    this.refs.modal.open();
	  },
	
	  _deleteModal: function() {
	    console.log("Got delete modal event!...");
	    RegistryServices.deletePlan(this.props.id).done(() => {	    
				     	console.log("Done deleting plan with id: ", this.props.id);
		    });
	    this.refs.modal.close();
	    
	    var router = Router.create({});
	    router.transitionTo('/');
	  },
	  
	  _cancelModal: function() {
	    console.log("Got cancel modal event!...");
	    this.refs.modal.close();
	  },
	  
    onEditPlan: function() {
    
        console.log("Got Edit Plan event!...", this.props);
	    var router = Router.create({});
	    router.transitionTo('/editPlan/' + this.props.id);
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
      
      var costPrice = '0.0 USD';
      var unit = 'MONTHLY';
      if ( typeof(this.props.metadata.costs[0]) != "undefined" ) {
      
      	unit      = this.props.metadata.costs[0].unit;
      	costPrice = JSON.stringify(this.props.metadata.costs[0].amount).replace(/\"/g, '');
        costPrice = costPrice.substr(1, costPrice.length - 2).split(':').reverse().join(' ').toUpperCase();
        
      }
      
      return (
        <div className="plan paxl">
          <p className="type-dark-1 mvn em-high mts">
            {this.props.name}
          </p>
          
    	    <div className="media-body media-middle txt-r">
	            <div className="btn-group" role="group" aria-label="...">
                  
                  
                  <DefaultButton id='openEditButton' className="btn btn-default" onClick={this.onEditPlan}>Edit Plan</DefaultButton>
                  <DefaultButton id='openDeleteButton' className="btn btn-default type-error-4  mls" onClick={this._openDeleteModal}>Delete Plan</DefaultButton>
			        <Modal title='Delete Service!' isOpen={this._openModal} onRequestClose={this._cancelModal} ref='modal' className='optional-custom-class'>
			          <ModalBody>  Confirm deletion of Plan: { this.props.name } </ModalBody>
			          <ModalFooter>
			            <DefaultButton id='cancelButton' onClick={this._cancelModal}>Cancel</DefaultButton>
			            <DefaultButton id='deleteButton' onClick={this._deleteModal}>Delete</DefaultButton>
			          </ModalFooter>
			        </Modal>
          		 
                </div>
              </div>
          
          
          <p className="type-dark-4 mvn">{ this.props.description }</p>
          <p className="type-dark-4 mvn type-sm mtl">
	        <p className="mvn type-dark-4 type-xs em-alt em-default label-alt">
	          Free Plan: {this.props.free? "true" : "false"}
	        </p>
	        <p className="type-dark-4  mvn type-sm mtl">            
              Cost : { costPrice }, charged {unit }
          	</p>
          </p>
          
          <p className="type-dark-4 mvn type-sm mtl">
            <span className="mrl em-alt">
              Categories:
              <InlineList divider>
              
                {
		          this.props.metadata.bullets.map(function(bullet) {
		            return <ListItem> {bullet}</ListItem>
		          })
		        }
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
