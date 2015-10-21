/** @jsx React.DOM */
var React = require('react');

var RegistryServices = require('../shared/registryServices.jsx');

(function () {
  'use strict';
  
      var CredentialEditor = React.createClass({
        getInitialState: function() {
          console.log("CredsEditor: Incoming props: ", this.props);
          
          var credentialsList = [ { cname: "testurl", cvalue : "http://www.test.com" } ];
             
	      RegistryServices.getCredentialsForPlans(this.props.planId).done(this.mapCredentials);
          return {credentialEntrylist: credentialsList};
        },
        
        mapCredentials: function(credAttribs) {
            var credentialEntrylist = [];
        	for(var key in credAttribs) {
			        credentialEntrylist.push( { cname: key, cvalue: credAttribs[key] } );
			    }
			this.setState ( { credentialEntrylist: credentialEntrylist }); 
        },
        handleNewRowSubmit: function( newcredentialEntry ) {
          this.setState( {credentialEntrylist: this.state.credentialEntrylist.concat([newcredentialEntry])} );
        },
        handleCredentialEntryRemove: function( credentialEntry ) {
          
          var index = -1; 
          var clength = this.state.credentialEntrylist.length;
          for( var i = 0; i < clength; i++ ) {
            if( this.state.credentialEntrylist[i].cname === credentialEntry.cname ) {
              index = i;
              break;
            }
          }
          this.state.credentialEntrylist.splice( index, 1 );  
          this.setState( {credentialEntrylist: this.state.credentialEntrylist} );
        },
        toString: function() {
	            //var output = '{';
	            var output = { };
			    console.log("Incoming Credentials editor state: " , this.state);
			    for (var i = 0; i < this.state.credentialEntrylist.length; i++) { 
			      var entry = this.state.credentialEntrylist[i];
			      
		        //if (output != '{')
		        //  output = output + ',';
		           
		           var name = entry['cname'];
		           var val = entry['cvalue'];
		           console.log("Incoming Credentials editor current entry: " , entry);
			       //output +=  '"' + name  + '" : "' +  val + '"';
			       output[name] = val;
			    }
			    //output += '}';
			    console.log("Credentials editor state: " , output);
			    return output;
			
        },
        render: function() {
        
          console.log("Inside render of the CredentialEditor...", this.state);        
        
          var tableStyle = {width: '100%'};
          var leftTdStyle = {width: '50%',padding:'20px',verticalAlign: 'top'};
          var rightTdStyle = {width: '50%',padding:'20px',verticalAlign: 'top'};
          return ( 
            <table >
              <tr>
                <td>
                  <CredentialEntryList clist={this.state.credentialEntrylist}  onCredentialEntryRemove={this.handleCredentialEntryRemove}/>
                </td>
                <td style={rightTdStyle}>
                  <NewCredentialEntryRow onRowSubmit={this.handleNewRowSubmit}/>
                </td>
              </tr>
          </table>
          );
        }
      });
      
      var CredentialEntryList = React.createClass({
        handleCredentialEntryRemove: function(credentialEntry){
          this.props.onCredentialEntryRemove( credentialEntry );
        },
        render: function() {
          var credentialEntries = [];
          var that = this; // TODO: Needs to find out why that = this made it work; Was getting error that onCredentialEntryDelete is not undefined
          this.props.clist.forEach(function(credentialEntry) {
            credentialEntries.push(<CredentialEntry credentialEntry={credentialEntry} onCredentialEntryDelete={that.handleCredentialEntryRemove} /> );
          });
          return ( 
            <div>
              <label for="credentials"><h4>Credentials</h4></label>
              <table className="table table-striped">
                <thead><tr><th>Name</th><th>Value</th><th>Action</th></tr></thead>
                <tbody>{credentialEntries}</tbody>
              </table>
            </div>
            );
        }
      });
      
      var CredentialEntry = React.createClass({
        handleRemoveCredentialEntry: function() {
          this.props.onCredentialEntryDelete( this.props.credentialEntry );
          return false;
        },
        render: function() {
          return (
            <tr>
              <td>{this.props.credentialEntry.cname}</td>
              <td>{this.props.credentialEntry.cvalue}</td>
              <td><input type="button"  className="btn btn-primary" value="Remove" onClick={this.handleRemoveCredentialEntry}/></td>
            </tr>
            );
        }
      });
      
      var NewCredentialEntryRow = React.createClass({
        handleSubmit: function() {
          var cname = this.refs.cname.getDOMNode().value;
          var cvalue = this.refs.cvalue.getDOMNode().value;
          var newrow = {cname: cname, cvalue: cvalue };
          this.props.onRowSubmit( newrow );
          
          this.refs.cname.getDOMNode().value = '';
          this.refs.cvalue.getDOMNode().value = '';
          return false;
        },
        render: function() {
          var inputStyle = {padding:'12px'}
          return ( 
            <div className="well">
              <h4>Add an entry</h4>
              <div className="input-group input-group-lg" style={inputStyle}>
                <input type="text"  className="form-control col-md-8"  placeholder="Name" ref="cname"/>
              </div>
              <div className="input-group input-group-lg" style={inputStyle}>
                <input type="text"  className="form-control col-md-8" placeholder="Value" ref="cvalue"/>
              </div>
              <div className="input-group input-group-lg" style={inputStyle}>
                <input type="submit"  className="btn btn-primary" value="Add Entry" onClick={this.handleSubmit}/>
              </div>
            
            </div>
            );
        }
});

module.exports = CredentialEditor;

}());