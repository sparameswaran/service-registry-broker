/** @jsx React.DOM */
var React = require('react');
var DefaultButton = require('pui-react-buttons').DefaultButton;

(function () {
  'use strict';
  
      var TagsEditor = React.createClass({
        getInitialState: function() {
          console.log("TagsEditor: Incoming tags list: ", this.props.tags);
          return {tagEntrylist:this.props.tags};
        },
        handleNewRowSubmit: function( newtagEntry ) {
          this.setState( {tagEntrylist: this.state.tagEntrylist.concat([newtagEntry])} );
        },
        handleTagsEntryRemove: function( tagEntry ) {
          
          var index = -1; 
          var clength = this.state.tagEntrylist.length;
          for( var i = 0; i < clength; i++ ) {
            if( this.state.tagEntrylist[i] === tagEntry ) {
              index = i;
              break;
            }
          }
          this.state.tagEntrylist.splice( index, 1 );  
          this.setState( {tagEntrylist: this.state.tagEntrylist} );
        },
        
        toString: function() {
            var output = [ ];
		    console.log("Incoming Tags editor state: " , this.state);
		    for (var i = 0; i < this.state.tagEntrylist.length; i++) { 
		      var entry = this.state.tagEntrylist[i];
	          
	           console.log("Incoming Tags editor current entry: " , entry);
		       output.push(entry.cvalue);
		    }
		    
		    //output = '[' + output + ']';
		    console.log("Tags editor state: " , output);
		    return output;		
        },
        
        render: function() {
          var tableStyle = {width: '100%'};
          var leftTdStyle = {width: '50%',padding:'20px',verticalAlign: 'top'};
          var rightTdStyle = {width: '50%',padding:'20px',verticalAlign: 'top'};
          return ( 
            <table >
              <tr>
                <td >
                  <TagsEntryList clist={this.state.tagEntrylist}  onTagsEntryRemove={this.handleTagsEntryRemove}/>
                </td>
                <td style={rightTdStyle}>
                  <NewTagsEntryRow onRowSubmit={this.handleNewRowSubmit}/>
                </td>
              </tr>
          </table>
          );
        }
      });
      
      var TagsEntryList = React.createClass({
        handleTagsEntryRemove: function(tagEntry){
          this.props.onTagsEntryRemove( tagEntry );
        },
        render: function() {
          var tagEntries = [];
          var that = this; // TODO: Needs to find out why that = this made it work; Was getting error that onTagsEntryDelete is not undefined
          this.props.clist.forEach(function(tagEntry) {
            tagEntries.push(<TagsEntry tagEntry={tagEntry} onTagsEntryDelete={that.handleTagsEntryRemove} /> );
          });
          return ( 
            <div>
              <label for="tags"><h4>Tags</h4></label>
              <table className="table table-striped">
                <thead><tr><th>Name</th><th>Action</th></tr></thead>
                <tbody>{tagEntries}</tbody>
              </table>
            </div>
            );
        }
      });
      
      var TagsEntry = React.createClass({
        handleRemoveTagsEntry: function() {
          this.props.onTagsEntryDelete( this.props.tagEntry );
          return false;
        },
        render: function() {
          return (
            <tr>
              <td>{this.props.tagEntry}</td>
              <td><DefaultButton type="button"  className="btn btn-primary  type-error-4" onClick={this.handleRemoveTagsEntry}> Remove </DefaultButton></td>
            </tr>
            );
        }
      });
      
      var NewTagsEntryRow = React.createClass({
        handleSubmit: function() {
          var cvalue = this.refs.cvalue.getDOMNode().value;
          var newrow = { cvalue };
          this.props.onRowSubmit( newrow );
          
          this.refs.cvalue.getDOMNode().value = '';
          return false;
        },
        render: function() {
          var inputStyle = {padding:'12px'}
          return ( 
            <div className="well">
              <h4>Add a tag</h4>
            <div className="input-group input-group-lg" style={inputStyle}>
                <input type="text"  className="form-control col-md-8" placeholder="Tag" ref="cvalue"/>
              </div>
              <div className="input-group input-group-lg" style={inputStyle}>
                <DefaultButton type="submit"  className="btn btn-primary" onClick={this.handleSubmit}> Add Tags </DefaultButton>
              </div>
            </div>
            );
        }
});

module.exports = TagsEditor;

}());