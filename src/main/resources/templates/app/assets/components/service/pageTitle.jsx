/** @jsx React.DOM */
var React = require('react');

// All the PUI
var MarketingH1 = require('pui-react-typography').MarketingH1;
var MarketingH2 = require('pui-react-typography').MarketingH2;
var SearchInput = require('pui-react-search-input').SearchInput;
var DefaultAltButton = require('pui-react-buttons').DefaultAltButton;

(function () {
  'use strict';

  var PageTitle = React.createClass({

    render: function() {
      return (
        <div className="page-title bg-neutral-11 pvxl">
          <div className="container">
            <div className="media">
              <div className="media-body media-middle">
                <div className="media">
                  <div className="media-left media-middle prxl">
                    <svg x="0px" y="0px" width="35px" height="30px" viewBox="0 0 33 30" enable-background="new 0 0 33 30">
                      <path id="XMLID_27_" fill-rule="evenodd" clip-rule="evenodd" fill="#7996D0" d="M16.5,27.4L3.6,11h25.9L16.5,27.4z M5.2,2h22.6
                        l2.3,7H2.9L5.2,2z M32.5,10.1C32.5,10.1,32.5,10.1,32.5,10.1c0-0.2,0-0.3,0-0.4c0,0,0,0,0-0.1l-3-9C29.3,0.3,28.9,0,28.5,0h-24
                        C4.1,0,3.7,0.3,3.5,0.7l-3,9c0,0,0,0,0,0.1c0,0.1,0,0.2,0,0.3c0,0,0,0,0,0.1c0,0.1,0,0.2,0.1,0.3c0,0,0,0.1,0,0.1c0,0,0,0.1,0.1,0.1
                        l15,19c0.2,0.2,0.5,0.4,0.8,0.4s0.6-0.1,0.8-0.4l15-19c0,0,0-0.1,0.1-0.1c0,0,0-0.1,0-0.1C32.4,10.3,32.5,10.2,32.5,10.1z"/>
                      <path id="XMLID_24_" opacity="0.5" fill-rule="evenodd" clip-rule="evenodd" fill="#7996D0" d="M16.5,1.9l5.5,8.2l-5.5,17.2
                        l-5.6-17.2L16.5,1.9z M23,10.2l5.9-8.9c0.2-0.2,0.1-0.5-0.1-0.7c-0.2-0.2-0.5-0.1-0.7,0.2l-5.6,8.4L16.9,1c-0.1-0.1-0.2,0-0.4,0h0
                        c-0.2,0-0.3-0.2-0.4,0l-5.8,8.1L4.9,0.7C4.8,0.5,4.5,0.4,4.2,0.6C4,0.7,3.9,1,4.1,1.3l5.8,8.9L16,29.2c0.1,0.2,0.3,0.3,0.5,0.3
                        c0.1,0,0.1,0,0.2,0c0.2-0.1,0.4-0.3,0.3-0.5c0,0,0,0,0,0L23,10.2z"/>
                      <rect id="XMLID_23_" fill="none" width="33" height="30"/>
                    </svg>
                  </div>
                  <div className="media-body media-middle">
                    <p className="type-sm em-alt label-alt mvn">
                      <span className="em-default type-accent-4">Services</span>
                      <span className="em-default type-dark-4 label-alt mhl">/</span>
                    </p>
                    <p className='h1 type-dark-1 mvn em-low'>{this.props.title}</p>
                    <p className="type-sm type-dark-2 mvn">
                      <span>Policy Retrieval System</span> 
                    </p>
                    <p className="type-sm type-dark-2 mvn">
                      <span className="type-dark-6">by</span> <span className="type-dark-2 em-high">Service Provider</span> <span className="type-dark-5 em-default">(Service Provider Inc.)</span>
                    </p>
                  </div>
                </div>
              </div>
              <div className="media-body media-middle txt-r">
                <div className="btn-group" role="group" aria-label="...">
                  <button type="button" className="btn btn-default">Edit</button>
                  <button type="button" className="btn btn-default type-error-4">Delete</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      );
    }
  });

  module.exports = PageTitle;

}());

