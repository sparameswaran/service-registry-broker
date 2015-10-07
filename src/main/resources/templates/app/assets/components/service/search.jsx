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

    render: function() {
      return (
        <div className="bg-dark-10 search">
          <div className="container">
            <SearchInput placeholder="Search for services ..."/>
          </div>
        </div>
      );
    }
  });

  module.exports = Search;

}());
