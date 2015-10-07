/** @jsx React.DOM */
var React = require('react');
var ReactRouter = require('react-router');

(function () {
  'use strict';

  var Link = ReactRouter.Link;

  var Header = React.createClass({

    render: function() {
      return (
      <div className='global-header'>
        <div className='container'>
          <a className="logo type-dark-11" href="/">
            <svg className="logo-mark" x="0px" y="0px" width="50px" height="50px" viewBox="0 0 50 50">
              <rect fill="#00786E" width="50" height="50"/>
              <path fill="#FFFFFF" d="M23.5,13.5H17V37h4V17h2c0.5,0,0.9,0,1.3,0c3.3,0.1,4.9,1.1,4.9,3.6c0,0.1,0,0.2,0,0.3
                c0,2.4-1.3,3.9-4.8,3.9c-0.3,0-0.9-0.1-0.9-0.1V28c0,0,0.5,0,0.9,0c5.1,0,8.7-2,8.7-7.1c0-0.1,0-0.2,0-0.3
                C33.1,15.4,29.2,13.5,23.5,13.5z"/>
            </svg>
            <span className="logo-type">Service Registry</span>
          </a>
        </div>
      </div>
      );
    }
  });

  module.exports = Header;

}());
