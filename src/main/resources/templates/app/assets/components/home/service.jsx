/** @jsx React.DOM */
var React = require('react');
var ReactRouter = require('react-router');
var Link = ReactRouter.Link;
var Navigation = require('react-router').Navigation;

var ClickableAltPanel = require('pui-react-panels').ClickableAltPanel;
var Flag = require('pui-react-media').Flag;
var Image = require('pui-react-images');
var TileLayout = require('pui-react-tile-layout');

(function () {
  'use strict';

  var Service = React.createClass({
    mixins: [Navigation],

    render: function() {
      return (
        <TileLayout.Item>
          <ClickableAltPanel className="mvn" onClick={this.transitionTo.bind(this, '/service', null, null)}>
            <div className="media">
              <div className="media-left media-middle">
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
              <div className="media-body">
                <h4 className="media-heading em-default type-dark-1">{this.props.title}</h4>
                <p className='mvn type-sm em-default type-dark-5'>Service description should be very short</p>
              </div>
            </div>
          </ClickableAltPanel>
        </TileLayout.Item>
      );
    }
  });

  module.exports = Service;

}());
