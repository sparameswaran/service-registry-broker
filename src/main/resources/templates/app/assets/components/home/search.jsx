/** @jsx React.DOM */
var React = require('react');

// All the PUI
var Icon = require('pui-react-iconography').Icon;
var Row = require('pui-react-grids').Row;
var Col = require('pui-react-grids').Col;
var Divider = require('pui-react-dividers').Divider;
var SearchInput = require('pui-react-search-input').SearchInput;

(function () {
  'use strict';

  
var Search = React.createClass({
  getInitialState: function () {
    return {
      filter: '',
      services: []
    }
  },

  updateFilter: function (event) {
    this.setState({ filter: event.target.value });
    console.info("Incoming filter value is : " , this.state.filter);
  },

  render: function () {
    console.log("Inside render for filtering search, this.props contains: ", this.props);
    
    var allServices = this.props.services;
    var filterRegex = new RegExp(this.state.filter, "i");
    var listServices = allServices.map(function (service) {
      var linkAddr =  '/#/service/' + service.id;
      console.info("Incoming service inside map: " , service);
      return service.name.match(filterRegex) && <li> <a class='btn' href={linkAddr} > { service.name }</a> </li>;
          
    });

	if (this.state.filter == '')
	    listServices = <div/>
	     
    return (
      <div>
        <SearchInput placeholder='Filter by...' onChange={this.updateFilter}/>        
          {listServices}
      </div>
    )
  }
});

  module.exports = Search;

}());
