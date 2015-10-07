/** @jsx React.DOM */
var React = require('react');

// All the PUI
var MarketingH1 = require('pui-react-typography').MarketingH1;
var MarketingH2 = require('pui-react-typography').MarketingH2;
var SearchInput = require('pui-react-search-input').SearchInput;

(function () {
  'use strict';

  var PageTitle = React.createClass({

    render: function() {
      return (
        <div className="page-title bg-neutral-11 pvxl">
          <div className="container">
            <div className="media">
              <div className="media-body media-middle">
                <p className='h1 type-dark-1 mvn em-low'>Services</p>
              </div>
              <div className="media-body media-middle">
                <SearchInput placeholder="Search for services ..." />
              </div>
            </div>
          </div>
        </div>
      );
    }
  });

  module.exports = PageTitle;

}());

