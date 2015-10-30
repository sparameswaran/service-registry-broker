/** @jsx React.DOM */
var React = require('react');
var _ = require('lodash');

// PUI
var Divider = require('pui-react-dividers').Divider;
var BaseCollapse = require('pui-react-collapse').BaseCollapse;
var Label = require('pui-react-labels').Label;
var InlineList = require('pui-react-lists').InlineList;
var ListItem = require('pui-react-lists').ListItem;
var StepList = require('pui-react-lists').StepList;

(function () {
    'use strict';

    var Credentials = React.createClass({

        getInitialState: function () {
            return {
                data: {
                    attributes: []
                }
            };
        },

        render: function () {
            console.log("Inside credential: " + this.props);

            //        var attributeList = _.mapKeys(this.props.creds, function(value, key) {
            //console.log("Key is ", key, " and value is ", value); 
            //             return <ul> <li> key </li>  <li> value </li> </ul>} ) ;

            //var credEntry = _.mapKeys(this.props.creds, function(attrib1, attrib2) {

            var pairs = [];
            for (var key in this.props.creds) {
                pairs.push(<tr>
                    <td width="130">
                        {key}
                    </td>
                    <td>
                        {this.props.creds[key]}
                    </td>
                </tr>);
            }
            console.log("Pairs contains: ", pairs);

            return (
                <div className="cred paxl">
                    <p className="mvn type-dark-4 type-xs em-alt em-default label-alt">Credential</p>
                    <p className="type-dark-4 mvn type-sm mtl">

                        <table >
                            <tr>
                                <th width="130">Name</th>
                                <th>Value</th>
                            </tr>

                            {pairs}
                        </table>
                    </p>

                </div>
            );
        }
    });

    module.exports = Credentials;

}());
