/** @jsx React.DOM */
var React = require('react');

// All the PUI
var Icon = require('pui-react-iconography').Icon;
var Row = require('pui-react-grids').Row;
var Col = require('pui-react-grids').Col;
var Divider = require('pui-react-dividers').Divider;
var TileLayout = require('pui-react-tile-layout');
var ClickableAltPanel = require('pui-react-panels').ClickableAltPanel;
var MarketingH1 = require('pui-react-typography').MarketingH1;
var SearchInput = require('pui-react-search-input').SearchInput;

var Service = require('./service.jsx');

(function () {
  'use strict';

  var Services = React.createClass({

    render: function() {
      return (
        <div className="pvxl">
          <div className="container">
            <TileLayout columns={{xs: 1, sm: 2, md: 3}}>

                <Service title="Big Database" />
                <Service title="Policy Interface" />
                <Service title="EDMSRetreiveInterface" />
                <Service title="Content Retrieval" />
                <Service title="Something Else" />
                <Service title="Big Database" />
                <Service title="Big Database" />
                <Service title="Big Database" />

            </TileLayout>
          </div>
        </div>
      );
    }
  });

  module.exports = Services;

}());
